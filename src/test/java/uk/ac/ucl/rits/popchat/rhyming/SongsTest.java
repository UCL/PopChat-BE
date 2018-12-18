package uk.ac.ucl.rits.popchat.rhyming;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SongsTest {

	@Autowired
	private SongRepository songRepo;

	/**
	 * Ensure that Lyrics.lyricsAt() return lyrics from the right time segment. This
	 * is also serving to test the parsing of the LRC
	 */
	@Test
	public void testLyricsAt() {
		List<Song> songs = songRepo.findByTitleIgnoreCase("Alexander Hamilton");

		// There should only be one song called Hamilton
		assert (songs.size() == 1);
		Song song = songs.get(0);
		Lyrics lyrics = new Lyrics(song);
		String line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(13));

		String correct_line = "Forgotten spot in the Caribbean by providence";
		assert (correct_line.equals(line));

		line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(0));
		assert (line == null);

		line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(3));
		assert (line == null);

		line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(4));
		correct_line = "How does a bastard, orphan, son of a whore and a";
		assert (line.equals(correct_line));
		
		
		line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(4000));
		correct_line = "Alexander Hamilton!";
		assert (line.equals(correct_line));
	}

	@Test
	public void testGetSegment() {
		List<Song> songs = songRepo.findByTitleIgnoreCase("Alexander Hamilton");

		// There should only be one song called Hamilton
		assert (songs.size() == 1);
		Song song = songs.get(0);

		Lyrics lyrics = new Lyrics(song);

		Duration dur = Duration.ofSeconds(40);

		Lyrics subSong = lyrics.getSegment(dur);

		Duration length = subSong.getDuration();

		assert (dur.minus(length).abs().getSeconds() <= 10);

	}
}
