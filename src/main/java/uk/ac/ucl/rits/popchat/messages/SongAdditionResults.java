package uk.ac.ucl.rits.popchat.messages;

/**
 * Returns the data back to the user about whether a song was added
 * successfully.
 *
 * @author RSDG
 *
 */
public class SongAdditionResults {

    private boolean valid;
    private String  titleErrorMessage;
    private String  artistErrorMessage;
    private String  yearErrorMessage;
    private String  videoErrorMessage;
    private String  lyricsErrorMessage;

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the titleErrorMessage
     */
    public String getTitleErrorMessage() {
        return titleErrorMessage;
    }

    /**
     * @param titleErrorMessage the titleErrorMessage to set
     */
    public void setTitleErrorMessage(String titleErrorMessage) {
        this.titleErrorMessage = titleErrorMessage;
    }

    /**
     * @return the artistErrorMessage
     */
    public String getArtistErrorMessage() {
        return artistErrorMessage;
    }

    /**
     * @param artistErrorMessage the artistErrorMessage to set
     */
    public void setArtistErrorMessage(String artistErrorMessage) {
        this.artistErrorMessage = artistErrorMessage;
    }

    /**
     * @return the yearErrorMessage
     */
    public String getYearErrorMessage() {
        return yearErrorMessage;
    }

    /**
     * @param yearErrorMessage the yearErrorMessage to set
     */
    public void setYearErrorMessage(String yearErrorMessage) {
        this.yearErrorMessage = yearErrorMessage;
    }

    /**
     * @return the videoErrorMessage
     */
    public String getVideoErrorMessage() {
        return videoErrorMessage;
    }

    /**
     * @param videoErrorMessage the videoErrorMessage to set
     */
    public void setVideoErrorMessage(String videoErrorMessage) {
        this.videoErrorMessage = videoErrorMessage;
    }

    /**
     * @return the lyricsErrorMessage
     */
    public String getLyricsErrorMessage() {
        return lyricsErrorMessage;
    }

    /**
     * @param lyricsErrorMessage the lyricsErrorMessage to set
     */
    public void setLyricsErrorMessage(String lyricsErrorMessage) {
        this.lyricsErrorMessage = lyricsErrorMessage;
    }

}
