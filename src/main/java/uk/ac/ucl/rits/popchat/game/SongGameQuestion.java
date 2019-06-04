package uk.ac.ucl.rits.popchat.game;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Represents a particular question for a song.
 *
 * @author RSDG
 *
 */
@Entity
public class SongGameQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                          questionId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SongGame                     parentGame;

    private String                       questionText;
    private LocalTime                    startTime;
    private LocalTime                    endTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parentQuestion")
    private List<SongGameQuestionOption> answers;

    /**
     * Create an empty SongGameQuestion.
     */
    public SongGameQuestion() {}

    /**
     * Create a SongGameQuestion.
     *
     * @param game      The parent game
     * @param text      The question text
     * @param startTime the start time for the question
     * @param endTime   the end time for the question
     * @param answers   List of all answers
     */
    public SongGameQuestion(SongGame game, String text, LocalTime startTime, LocalTime endTime,
            List<SongGameQuestionOption> answers) {
        this.parentGame = game;
        this.questionText = text;
        this.startTime = startTime;
        this.endTime = endTime;
        this.answers = new ArrayList<>(answers);
        this.answers.forEach(a -> a.setParentQuestion(this));
    }

    /**
     * @return the questionId
     */
    public int getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId the questionId to set
     */
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    /**
     * @return the parentGame
     */
    public SongGame getParentGame() {
        return parentGame;
    }

    /**
     * @param parentGame the parentGame to set
     */
    public void setParentGame(SongGame parentGame) {
        this.parentGame = parentGame;
    }

    /**
     * @return the questionText
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * @param questionText the questionText to set
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * @return the answers
     */
    public List<SongGameQuestionOption> getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(List<SongGameQuestionOption> answers) {
        this.answers = answers;
    }

    /**
     * @return the startTime
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

}
