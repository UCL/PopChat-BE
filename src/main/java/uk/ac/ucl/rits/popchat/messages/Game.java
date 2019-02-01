package uk.ac.ucl.rits.popchat.messages;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    private String    url;
    private LocalTime startTime, endTime;

    /**
     * @return the startTime
     */
    public int getStartTime() {
        return startTime.toSecondOfDay();
    }

    /**
     * @return the endTime
     */
    public int getEndTime() {
        return endTime.toSecondOfDay();
    }

    /**
     * @return the questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    private List<Question> questions;

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
        this.startTime = fragment.getStartTime();
        this.endTime = fragment.getEndTime();

        Rhymes rhymes = Rhymes.getRhymes();
        Set<String> words = rhymes.createRhymesWithGame(fragment);
        String[] wordsArray = words.toArray(new String[0]);
        int key = (int) (Math.random() * wordsArray.length);

        Set<String> allLyrics = new HashSet<String>(fragment.getWords());
        allLyrics.removeAll(words);

        List<String> uniqueLyrics = new ArrayList<>(allLyrics);

        this.questions = new ArrayList<>();
        for (int i = 0; i < wordsArray.length; i++) {
            if (i == key) {
                continue;
            }
            this.questions.add(generateQuestion(wordsArray[key], wordsArray[i], uniqueLyrics));
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
    private Question generateQuestion(String keyWord, String answer, List<String> lyrics) {
        String question = String.format("Which of the following words rhymes with %s?", keyWord);

        List<QuestionOption> answers = new ArrayList<>();
        answers.add(new QuestionOption(answer, true));

        for (int i = 0; i < 3; i++) {
            answers.add(new QuestionOption(lyrics.get((int) (Math.random() * lyrics.size())), false));
        }

        Collections.shuffle(answers);

        return new Question(question, answers);
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
