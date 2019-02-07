package uk.ac.ucl.rits.popchat.messages;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Results table row for admin display.
 *
 * @author RSDG
 *
 */
public class ResultsTableRow {

    @NotNull
    private String        username;
    @NotNull
    private String        songTitle;
    @NotNull
    private String        question;
    @NotNull
    private LocalDateTime questionStartTime;
    @NotNull
    private String        answer;
    @NotNull
    private LocalDateTime answerSelectTime;
    @NotNull
    private boolean       answerCorrect;
    @NotNull
    private List<String>  allAnswers;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the songTitle
     */
    public String getSongTitle() {
        return songTitle;
    }

    /**
     * @param songTitle the songTitle to set
     */
    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    /**
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return the questionStartTime
     */
    public LocalDateTime getQuestionStartTime() {
        return questionStartTime;
    }

    /**
     * @param questionStartTime the questionStartTime to set
     */
    public void setQuestionStartTime(LocalDateTime questionStartTime) {
        this.questionStartTime = questionStartTime;
    }

    /**
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return the answerSelectTime
     */
    public LocalDateTime getAnswerSelectTime() {
        return answerSelectTime;
    }

    /**
     * @param answerSelectTime the answerSelectTime to set
     */
    public void setAnswerSelectTime(LocalDateTime answerSelectTime) {
        this.answerSelectTime = answerSelectTime;
    }

    /**
     * @return the answerCorrect
     */
    public boolean isAnswerCorrect() {
        return answerCorrect;
    }

    /**
     * @param answerCorrect the answerCorrect to set
     */
    public void setAnswerCorrect(boolean answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    /**
     * @return the allAnswers
     */
    public List<String> getAllAnswers() {
        return allAnswers;
    }

    /**
     * @param allAnswers the allAnswers to set
     */
    public void setAllAnswers(List<String> allAnswers) {
        this.allAnswers = allAnswers;
    }

}
