package uk.ac.ucl.rits.popchat.messages;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.rits.popchat.game.SongGameQuestion;

/**
 * Represents a single question in a song game.
 *
 * @author RSDG
 *
 */
public class Question {

    private int                  questionId;
    private String               questionText;
    private List<QuestionOption> answers;

    /**
     * Create an empty question.
     */
    public Question() {}

    /**
     * Create a Question from a record.
     *
     * @param q database record to copy
     */
    public Question(SongGameQuestion q) {
        this.questionText = q.getQuestionText();
        this.answers = new ArrayList<>();
        this.questionId = q.getQuestionId();
        q.getAnswers().forEach(a -> this.answers.add(new QuestionOption(a)));
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

}
