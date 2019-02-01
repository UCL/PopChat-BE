package uk.ac.ucl.rits.popchat.messages;

/**
 * A possible answer to a single question for a song game.
 *
 * @author RSDG
 *
 */
public class QuestionOption {
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
     * @param value   The text
     * @param correct If the answer is correct
     */
    public QuestionOption(String value, boolean correct) {
        this.value = value;
        this.correct = correct;
    }
}
