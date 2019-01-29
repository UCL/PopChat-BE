package uk.ac.ucl.rits.popchat.messages;

import uk.ac.ucl.rits.popchat.songs.Song;

/**
 * This is the summary information sent about a song.
 *
 * @author RSDG
 *
 */
public class SongResponse {

    private long   id;
    private String title;
    private String artist;

    /**
     * Create a new SongResponse.
     */
    public SongResponse() {}

    /**
     * Copy constructor.
     *
     * @param s Song to copy
     */
    public SongResponse(Song s) {
        this.id = s.getId();
        this.title = s.getTitle();
        this.artist = s.getArtist();
    }

    /**
     * Get the Id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id Id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the artist.
     *
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Set artist.
     *
     * @param artist artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

}
