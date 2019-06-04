package uk.ac.ucl.rits.popchat.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A possible answer to a question.
 *
 * @author RSDG
 *
 */
@Entity
public class SongGameQuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int              optionId;

    @ManyToOne
    private SongGameQuestion parentQuestion;
    private String           value;
    private boolean          correct;

    /**
     * Create an empty option.
     */
    public SongGameQuestionOption() {}

    /**
     * Create a song game question option.
     *
     * @param value   The text
     * @param correct If the answer is correct
     */
    public SongGameQuestionOption(String value, boolean correct) {
        this.value = value;
        this.correct = correct;
    }

    /**
     * @return the optionId
     */
    public int getOptionId() {
        return optionId;
    }

    /**
     * @param optionId the optionId to set
     */
    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    /**
     * @return the parentQuestion
     */
    public SongGameQuestion getParentQuestion() {
        return parentQuestion;
    }

    /**
     * @param parentQuestion the parentQuestion to set
     */
    public void setParentQuestion(SongGameQuestion parentQuestion) {
        this.parentQuestion = parentQuestion;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the correct
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * @param correct the correct to set
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

}
