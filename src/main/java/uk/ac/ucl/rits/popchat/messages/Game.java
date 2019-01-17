package uk.ac.ucl.rits.popchat.messages;

import java.time.Duration;
import java.util.Set;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;
import uk.ac.ucl.rits.popchat.songs.Lyrics;

public class Game {

	public Game() {
	}

	public Game(Lyrics lyrics) {
		Lyrics fragment = lyrics.getSegment(Duration.ofSeconds(40));
		Rhymes rhymes = Rhymes.getRhymes();
		Set<String> words = rhymes.createRhymesWithGame(fragment);

	}
}
