package uk.ac.ucl.rits.popchat.messages;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.rits.popchat.game.SongGame;

/**
 * Create a game for a user to play. A game is a song fragment with a set of
 * questions.
 *
 * @author RSDG
 *
 */
public class Game {

    private String         url;
    private int            gameId;
    private int            startTime, endTime;
    private List<Question> questions;

    /**
     * Create a new game.
     */
    public Game() {}

    /**
     * Copy constructor from database record.
     *
     * @param g SongGame to copy
     */
    public Game(SongGame g) {
        this.url = g.getSong().getVideo();
        this.gameId = g.getGameId();
        this.startTime = g.getSongStartSeconds();
        this.endTime = g.getSongEndSeconds();
        this.questions = new ArrayList<>();
        g.getQuestions().forEach(q -> this.questions.add(new Question(q)));
    }

    /**
     * Get the video URL.
     *
     * @return video url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @return the startTime
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * @return the questions
     */
    public List<Question> getQuestions() {
        return questions;
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

}
