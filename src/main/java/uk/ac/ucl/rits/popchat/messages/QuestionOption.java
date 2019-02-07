package uk.ac.ucl.rits.popchat.messages;

import uk.ac.ucl.rits.popchat.game.SongGameQuestionOption;

/**
 * A possible answer to a single question for a song game.
 *
 * @author RSDG
 *
 */
public class QuestionOption {

    private int     optionId;
    private String  value;
    private boolean correct;

    /**
     * Create a blank question option.
     */
    public QuestionOption() {}

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

    /**
     * Create a question option.
     *
     * @param o the option to copy
     */
    public QuestionOption(SongGameQuestionOption o) {
        this.value = o.getValue();
        this.correct = o.isCorrect();
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

}
