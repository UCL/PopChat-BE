package uk.ac.ucl.rits.popchat.messages;

import java.time.Duration;
import java.util.Set;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;
import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;

public class Game {

	public String url;

	public Game() {
	}

	public Game(Song song, Lyrics lyrics) {
		this.url = song.getVideo();

		Lyrics fragment = lyrics.getSegment(Duration.ofSeconds(40));
		Rhymes rhymes = Rhymes.getRhymes();
		Set<String> words = rhymes.createRhymesWithGame(fragment);
		String[] wordsArray = words.toArray(new String[0]);
	}
}
