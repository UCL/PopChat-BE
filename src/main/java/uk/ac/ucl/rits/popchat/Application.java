package uk.ac.ucl.rits.popchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.atrox.haikunator.Haikunator;
import uk.ac.ucl.rits.popchat.security.ResourceServerConfiguration;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;
import uk.ac.ucl.rits.popchat.users.PopUser;
import uk.ac.ucl.rits.popchat.users.UserRepository;

/**
 * The Application is the entry point for the Application. It sets up the Spring
 * environment, as well as pre-loads songs into the database.
 *
 * @author RSDG
 *
 */
@SpringBootApplication
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * This initialises the repository with songs based on those provided in dbInit.
	 * Songs are not double added to database based on the URL for the video.
	 *
	 * @param repository   The SongRepository to add words too
	 * @param initDataRoot The directory to look for JSON files describing songs in
	 * @return CommandLineRunner to initialise the database
	 */
	@Bean
	public CommandLineRunner setup(SongRepository repository,
			@Value("${initialSongDataDirectory}") String initDataRoot) {
		return (args) -> {
			List<String> songEntries = new ArrayList<>();

			try (BufferedReader read = new BufferedReader(
					new InputStreamReader(Application.class.getResourceAsStream(initDataRoot)))) {
				read.lines().filter(name -> name.endsWith(".json")).forEach(songEntries::add);
			} catch (IOException e) {
				log.error(String.format("Failed in read %s directory to find songs", initDataRoot));
			}

			// Save songs to the database
			int songsAdded = 0;
			int songsPresent = 0;
			for (String songFile : songEntries) {

				// Read and deserialise the Song
				String filename = String.format("%s/%s", initDataRoot, songFile);
				InputStream in = Application.class.getResourceAsStream(filename);
				ObjectMapper objectMapper = new ObjectMapper();
				Song song = objectMapper.readValue(in, Song.class);

				// Ensure the song doesn't already exist
				if (repository.findFirst1ByVideo(song.getVideo()) == null) {
					repository.save(song);
					songsAdded++;
					log.trace("Added song: " + song.toString());
				} else {
					songsPresent++;
					log.trace("Already present song: " + song.toString());
				}
			}
			log.info(String.format("Total songs: %d, added %d on startup", songsPresent + songsAdded, songsAdded));

		};
	}

	/**
	 * Ensure that passwords should fit in the database.
	 * Since a password contains both the actual password and the data used to 
	 * generate it, you need all of the data concatenated to fit in the column.
	 * 
	 * @param saltLength Length of generated salt
	 * @param hashLength Length of password hash
	 * @return CommandLineRunner to perform this check. Crashes startup if the check fails.
	 */
	@Bean
	public CommandLineRunner ensureValidSizes(@Value("${salt.length}") int saltLength,
			@Value("${hash.length}") int hashLength) {
		return (args) -> {
			if (saltLength + hashLength > 600) {
				throw new IllegalArgumentException(
						String.format("Cannot start application because salt.length + hash.length > 600: %d + %d = %d",
								saltLength, hashLength, saltLength + hashLength));
			}
		};
	}

	/**
	 * Ensure that an administrator user exists on start up. If it doesn't, create a
	 * new one and print its logon details to the screen.
	 * 
	 * @param userRepo        User repository
	 * @param passwordEncoder PasswordEncoder
	 * @param username        Admin username to set
	 * @return CommandLineRunner to perform this action
	 */
	@Bean
	public CommandLineRunner ensureAdmin(UserRepository userRepo, PasswordEncoder passwordEncoder,
			@Value("${default.admin.username}") final String username) {
		return (args) -> {

			String uname = username;
			PopUser existingUser = userRepo.findByUsername(uname);
			if (existingUser != null && existingUser.getIsAdmin()) {
				log.info("Admin user already present");
			} else {
				Haikunator haik = new Haikunator();
				haik.setTokenLength(1);
				while (existingUser != null) {
					uname = haik.haikunate();
					existingUser = userRepo.findByUsername(uname);
				}
				try {
					String password = haik.haikunate();
					PopUser newUser = new PopUser(uname, passwordEncoder.encode(password), true);
					userRepo.save(newUser);
					log.info(String.format("Created admin with username %s and password %s", uname, password));
				} catch (IllegalStateException e) {
					log.error("Failed to create new user", e);
					throw new RuntimeException(
							"Sorry. New users cannot be created at this time. Please contact support.");
				}
			}
		};
	}

	/**
	 * Ensure that an OAuth2 client exists on startup.
	 * 
	 * @param service         The Client Service
	 * @param passwordEncoder PasswordEncoder
	 * @param clientName      The name of the default client to create
	 * @return CommandLineRunner to perform this action
	 */
	@Bean
	public CommandLineRunner ensureClient(final JdbcClientDetailsService service, final PasswordEncoder passwordEncoder,
			@Value("${default.oauth2.client}") String clientName) {
		return (args) -> {
			int numClients = service.listClientDetails().size();
			if (numClients > 0) {
				// If a client already exists you don't need to do anything
				log.info("Oauth2 clients already present");
			} else {
				// Create a new client that can login with a password and supports refresh tokens
				BaseClientDetails coreClient = new BaseClientDetails(clientName,
						ResourceServerConfiguration.RESOURCE_ID, "read,write,trust", "password,refresh_token",
						"ROLE_BASIC,ROLE_ADMIN");
				
				// Generate a random password
				Haikunator haik = new Haikunator();
				haik.setTokenLength(1);
				String password = haik.haikunate();
				coreClient.setClientSecret(passwordEncoder.encode(password));
				service.addClientDetails(coreClient);
				log.info(String.format("Created an OAuth2 client with name: %s and password: %s", clientName,
						password));
				assert (service.listClientDetails().size() == 1);
			}
		};
	}

}
