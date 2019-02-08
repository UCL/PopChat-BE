package uk.ac.ucl.rits.popchat.game;

import java.time.Duration;
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
        Set<Set<String>> multiWords = rhymes.createRhymesWithGame(fragment);

        this.questions = new ArrayList<>();
        for (Set<String> words : multiWords) {

            String[] wordsArray = words.toArray(new String[0]);
            int key = (int) (Math.random() * wordsArray.length);

            Set<String> allLyrics = new HashSet<String>(fragment.getWords());
            allLyrics.removeAll(words);

            List<String> uniqueLyrics = new ArrayList<>(allLyrics);

            for (int i = 0; i < wordsArray.length; i++) {
                if (i == key) {
                    continue;
                }
                this.questions.add(generateQuestion(wordsArray[key], wordsArray[i], uniqueLyrics));
            }
        }
    }

    /**
     * Generate a new question.
     *
     * @param keyWord Key word
     * @param answer  the right answer
     * @param lyrics  wrong answer suggestions
     * @return A question
     */
    private SongGameQuestion generateQuestion(String keyWord, String answer, List<String> lyrics) {
        String question = String.format("Which of the following words rhymes with %s?", keyWord);

        List<SongGameQuestionOption> answers = new ArrayList<>();
        answers.add(new SongGameQuestionOption(answer, true));

        for (int i = 0; i < 3; i++) {
            answers.add(new SongGameQuestionOption(lyrics.get((int) (Math.random() * lyrics.size())), false));
        }

        Collections.shuffle(answers);

        return new SongGameQuestion(this, question, answers);
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
