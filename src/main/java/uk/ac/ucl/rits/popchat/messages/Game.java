package uk.ac.ucl.rits.popchat.messages;

import java.time.Duration;
import java.util.Set;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;
import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;

/**
 * Create a game for a user to play. A game is a song fragment with a set of
 * questions.
 *
 * @author RSDG
 *
 */
public class Game {

    private String url;

    /**
     * Create a new game.
     */
    public Game() {}

    /**
     * Create a new game from a song with its words.
     *
     * @param song   The song
     * @param lyrics The songs lyrics
     */
    public Game(Song song, Lyrics lyrics) {
        this.url = song.getVideo();

        Lyrics fragment = lyrics.getSegment(Duration.ofSeconds(40));
        Rhymes rhymes = Rhymes.getRhymes();
        Set<String> words = rhymes.createRhymesWithGame(fragment);
        String[] wordsArray = words.toArray(new String[0]);
    }

    /**
     * Get the video URL.
     *
     * @return video url
     */
    public String getUrl() {
        return this.url;
    }

}
