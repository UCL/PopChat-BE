package uk.ac.ucl.rits.popchat.messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single question in a song game.
 *
 * @author RSDG
 *
 */
public class Question {
    private String               questionText;
    private List<QuestionOption> answers;

    /**
     * Create an empty question.
     */
    public Question() {}

    /**
     * Create a Question.
     *
     * @param text    The question text
     * @param answers List of all answers
     */
    public Question(String text, List<QuestionOption> answers) {
        this.questionText = text;
        this.answers = new ArrayList<>(answers);
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
    public List<QuestionOption> getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(List<QuestionOption> answers) {
        this.answers = answers;
    }

}
