package uk.ac.ucl.rits.popchat.game;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import uk.ac.ucl.rits.popchat.users.PopUser;

/**
 * An answer to a song game question.
 *
 * @author RSDG
 *
 */
@Entity
public class SongGameResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                    responseId;

    @ManyToOne
    private SongGameQuestionOption questionOptionId;

    @ManyToOne
    private PopUser                user;

    private LocalDateTime          questionStartTime;
    private LocalDateTime          questionEndTime;

    /**
     * @return the responseId
     */
    public int getResponseId() {
        return responseId;
    }

    /**
     * @param responseId the responseId to set
     */
    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    /**
     * @return the questionOptionId
     */
    public SongGameQuestionOption getQuestionOptionId() {
        return questionOptionId;
    }

    /**
     * @param questionOptionId the questionOptionId to set
     */
    public void setQuestionOptionId(SongGameQuestionOption questionOptionId) {
        this.questionOptionId = questionOptionId;
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
     * @return the questionEndTime
     */
    public LocalDateTime getQuestionEndTime() {
        return questionEndTime;
    }

    /**
     * @param questionEndTime the questionEndTime to set
     */
    public void setQuestionEndTime(LocalDateTime questionEndTime) {
        this.questionEndTime = questionEndTime;
    }

}
