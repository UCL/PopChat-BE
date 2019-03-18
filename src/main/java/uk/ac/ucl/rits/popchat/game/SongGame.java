package uk.ac.ucl.rits.popchat.game;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.data.util.Pair;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;
import uk.ac.ucl.rits.popchat.songs.Lyrics;
import uk.ac.ucl.rits.popchat.songs.Song;
import uk.ac.ucl.rits.popchat.users.PopUser;

/**
 * A song game as stored in the database.
 *
 * @author RSDG
 *
 */
@Entity
public class SongGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                    gameId;

    @NotNull
    @ManyToOne
    private Song                   song;

    @NotNull
    @ManyToOne
    private PopUser                user;

    @NotNull
    private int                    songStartSeconds;

    @NotNull
    private int                    songEndSeconds;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parentGame")
    private List<SongGameQuestion> questions;

    /**
     * Create a new SongGame.
     */
    public SongGame() {}

    /**
     * Create a new game from a song with its words.
     *
     * @param song   The song
     * @param lyrics The songs lyrics
     */
    public SongGame(Song song, Lyrics lyrics) {
        this.song = song;

        Lyrics fragment = lyrics.getSegment(Duration.ofSeconds(40));
        this.songStartSeconds = fragment.getStartTime().toSecondOfDay();
        this.songEndSeconds = fragment.getEndTime().toSecondOfDay();

        Rhymes rhymes = Rhymes.getRhymes();
        List<Lyrics> questionSequence = fragment.fragment(2);

        this.questions = new ArrayList<>();
        for (Lyrics subFragment : questionSequence) {
            Set<Set<String>> multiWords = rhymes.createRhymesWithGame(subFragment);
            Pair<String, String> question = this.pickRandomPair(multiWords);
            if (question == null) {
                question = this.pickRandomQuestion(subFragment, rhymes, 10);
                if (question == null) {
                    continue;
                }
            }
            Set<String> uniqueLyrics = new HashSet<>(subFragment.getWords());
            uniqueLyrics.remove(question.getFirst());
            uniqueLyrics.remove(question.getSecond());

            this.questions.add(generateQuestion(question.getFirst(), question.getSecond(), subFragment.getStartTime(),
                    subFragment.getEndTime(), new ArrayList<>(uniqueLyrics), rhymes));

        }

    }

    /**
     * Try generate a random question based on any given word.
     *
     * @param lyrics   The lyrics of the song fragment
     * @param rhymes   Rhymes object
     * @param maxTries Number of times to try before giving up
     * @return Question and answer word
     */
    private Pair<String, String> pickRandomQuestion(Lyrics lyrics, Rhymes rhymes, int maxTries) {
        List<String> words = new ArrayList<>(new HashSet<>(lyrics.getWords()));
        Pair<String, String> question = null;

        for (int i = 0; i < maxTries && question == null; i++) {
            String ans = words.get((int) (Math.random() * words.size()));
            List<String> qs = new ArrayList<>(rhymes.rhymes(ans));
            if (qs.size() > 0) {
                question = Pair.of(qs.get((int) (Math.random() * qs.size())), ans);
            }
        }
        return question;
    }

    /**
     * Given sets of mutually rhyming words in a song, pick two words to use for a
     * question.
     *
     * @param multiWords Set of sets of rhyming words within the song.
     * @return A pair of words to use for a question and right answer.
     */
    private Pair<String, String> pickRandomPair(Set<Set<String>> multiWords) {
        int sets = multiWords.size();
        if (sets == 0) {
            return null;
        }
        int setPicked = (int) (Math.random() * sets);
        Set<String> words = null;
        int i = 0;
        for (Set<String> w : multiWords) {
            words = w;
            i++;
            if (i == setPicked) {
                break;
            }
        }
        if (words.size() < 2) {
            throw new IllegalStateException("Rhyming set had less than two words");
        }

        String[] wordsArray = words.toArray(new String[0]);
        int key = (int) (Math.random() * wordsArray.length);
        String question = wordsArray[key];

        words.remove(question);
        wordsArray = words.toArray(new String[0]);
        key = (int) (Math.random() * wordsArray.length);
        String answer = wordsArray[key];

        return Pair.of(question, answer);
    }

    /**
     * Generate a new question.
     *
     * @param keyWord   Key word
     * @param answer    the right answer
     * @param startTime When the question should start
     * @param endTime   When the question should end
     * @param lyrics    wrong answer suggestions
     * @param rhymes    Rhymes object
     * @return A question
     */
    private SongGameQuestion generateQuestion(String keyWord, String answer, LocalTime startTime, LocalTime endTime,
            List<String> lyrics, Rhymes rhymes) {
        String question = String.format("Which of the following words is in the song and rhymes with %s?", keyWord);

        List<SongGameQuestionOption> answers = new ArrayList<>();
        answers.add(new SongGameQuestionOption(answer, true));

        Set<String> rhymingWords = rhymes.rhymes(question);
        rhymingWords.removeAll(lyrics);

        if (rhymingWords.size() > 3) {
            // We have more options than we need. Delete all but 3 at random
            List<String> words = new ArrayList<String>(rhymingWords);
            while (words.size() > 3) {
                words.remove((int) (Math.random() * words.size()));
            }
            rhymingWords = new HashSet<>(words);
        } else if (rhymingWords.size() < 3) {
            // We need to add some non rhyming words to bolster the possible answers
            lyrics.removeAll(rhymingWords);
            while (rhymingWords.size() < 3 && lyrics.size() > 0) {
                rhymingWords.add(lyrics.remove((int) (Math.random() * lyrics.size())));
            }
        }

//        if (rhymingWords.size() < 3) {
//            // Not enough rhyming words or lyrics. Need to add random words
//            rhymes.
//        }

        for (String ans : rhymingWords) {
            answers.add(new SongGameQuestionOption(ans, false));
        }

        Collections.shuffle(answers);

        return new SongGameQuestion(this, question, startTime, endTime, answers);
    }

    /**
     * @return the gameId
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the song
     */
    public Song getSong() {
        return this.song;
    }

    /**
     * @param song the song to set
     */
    public void setSong(Song song) {
        this.song = song;
    }

    /**
     * @return the songStartTime
     */
    public int getSongStartSeconds() {
        return songStartSeconds;
    }

    /**
     * @param songStartSeconds the songStartTime to set
     */
    public void setSongStartSeconds(int songStartSeconds) {
        this.songStartSeconds = songStartSeconds;
    }

    /**
     * @return the songEndTime
     */
    public int getSongEndSeconds() {
        return songEndSeconds;
    }

    /**
     * @param songEndSeconds the songEndTime to set
     */
    public void setSongEndSeconds(int songEndSeconds) {
        this.songEndSeconds = songEndSeconds;
    }

    /**
     * @return the questions
     */
    public List<SongGameQuestion> getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(List<SongGameQuestion> questions) {
        this.questions = questions;
    }

    /**
     * @return the user
     */
    public PopUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(PopUser user) {
        this.user = user;
    }

}
