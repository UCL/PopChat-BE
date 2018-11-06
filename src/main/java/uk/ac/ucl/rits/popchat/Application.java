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

import com.fasterxml.jackson.databind.ObjectMapper;

import me.atrox.haikunator.Haikunator;
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
			for (String songFile : songEntries) {

				// Read and deserialise the Song
				String filename = String.format("%s/%s", initDataRoot, songFile);
				InputStream in = Application.class.getResourceAsStream(filename);
				ObjectMapper objectMapper = new ObjectMapper();
				Song song = objectMapper.readValue(in, Song.class);

				// Ensure the song doesn't already exist
				if (repository.findFirst1ByVideo(song.getVideo()) == null) {
					repository.save(song);
				} else {
					log.info(String.format("%s was already contained", song.toString()));
				}
			}

		};
	}

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

}
