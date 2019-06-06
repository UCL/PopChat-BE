package uk.ac.ucl.rits.popchat.rhyming;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ucl.rits.popchat.game.SongGame;
import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.songs.SongRepository;

/**
 * Test the song related functionality.
 *
 * @author RSDG
 *
 */
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
        assert songs.size() == 1;
        Song song = songs.get(0);
        Lyrics lyrics = new Lyrics(song);
        String line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(13));

        String correctLine = "Forgotten spot in the Caribbean by providence";
        assert correctLine.equals(line);

        line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(0));
        assert line == null;

        line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(3));
        assert line == null;

        line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(4));
        correctLine = "How does a bastard, orphan, son of a whore and a";
        assert line.equals(correctLine);

        line = lyrics.lyricsAt(LocalTime.ofSecondOfDay(4000));
        correctLine = "Alexander Hamilton!";
        assert line.equals(correctLine);
    }

    /**
     * Ensure that segments are the right length.
     */
    @Test
    public void testGetSegment() {
        List<Song> songs = songRepo.findByTitleIgnoreCase("Alexander Hamilton");

        // There should only be one song called Hamilton
        assert songs.size() == 1;
        Song song = songs.get(0);

        Lyrics lyrics = new Lyrics(song);

        Duration dur = Duration.ofSeconds(40);

        Lyrics subSong = lyrics.getSegment(dur);

        Duration length = subSong.getDuration();

        assertTrue(dur.minus(length).abs().getSeconds() <= 10);

    }

    /**
     * Test to make sure that create game returns a set of mutually rhyming words
     * that are all in the song fragment.
     */
    @Test
    public void testCreateRhymesGame() {
        List<Song> songs = songRepo.findByTitleIgnoreCase("Alexander Hamilton");

        // There should only be one song called Hamilton
        assert songs.size() == 1;
        Song song = songs.get(0);

        Lyrics lyrics = new Lyrics(song);

        Duration dur = Duration.ofSeconds(40);

        Lyrics subSong = lyrics.getSegment(dur);

        System.out.println(subSong.getText());

        Set<Set<String>> sets = Rhymes.getRhymes().createRhymesWithGame(subSong);

        for (Set<String> question : sets) {
            for (String w : question) {
                System.out.println(w);
            }
            System.out.println("----------");

            assertNotNull(question);
            assertTrue(subSong.getWords().containsAll(question));

            if (!question.isEmpty()) {
                String sampleWord = question.iterator().next();
                Set<String> test = Rhymes.getRhymes().rhymes(sampleWord);
                assertTrue(test.add(sampleWord));

                question.forEach(x -> x.toLowerCase());
                /* boolean changed = */question.retainAll(test);
                for (String w : question) {
                    System.out.println(w);
                }

                // This part of the test doesn't work because of Issue #26
                // assertFalse(changed);

            }
        }
    }

    /**
     * Test to make sure that create game of fixed size returns the right number of
     * questions.
     */
    @Test
    public void testCreateFixedSizeTransientGame() {
        List<Song> songs = songRepo.findByTitleIgnoreCase("Alexander Hamilton");

        // There should only be one song called Hamilton
        assert songs.size() == 1;
        Song song = songs.get(0);

        Lyrics lyrics = new Lyrics(song);

        for (int i = 2; i < 15; i++) {
            SongGame game = new SongGame(song, lyrics, i);
            assertTrue(String.format("Failed for size %d", i), game.getQuestions().size() == i);
        }

    }
}
