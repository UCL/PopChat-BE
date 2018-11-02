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

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;

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
}
