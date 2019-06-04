package uk.ac.ucl.rits.popchat.messages;

import java.time.LocalDateTime;

/**
 * An answer to a question done by the user.
 *
 * @author RSDG
 *
 */
public class GameAnswer {

    private int           optionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

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
     * @return the startTime
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
