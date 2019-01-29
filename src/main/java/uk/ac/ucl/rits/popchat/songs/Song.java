package uk.ac.ucl.rits.popchat.songs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * This represents a Song in the database.
 *
 * <p>
 * Since it is hard to identify what should be unique for a song, we use
 * generated ids to tell them apart.
 *
 * @author RSDG
 */
@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long   id;

    @NotNull
    private String title;
    @NotNull
    private String artist;
    @NotNull
    private int    year;
    @NotNull
    private String video;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String lyrics;

    /**
     * Create a new Song.
     */
    public Song() {}

    /**
     * Create a new song.
     *
     * @param title  Song title
     * @param artist Song artist
     * @param year   Release year
     * @param video  Youtube embed url
     */
    public Song(String title, String artist, int year, String video) {
        this(title, artist, year, video, null);
    }

    /**
     * Create a song.
     *
     * @param title  song title
     * @param artist song artist
     * @param year   song release year
     * @param video  song video embed url
     * @param lyrics song lyrics in LRC format
     */
    public Song(String title, String artist, int year, String video, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.video = video;
        this.lyrics = lyrics;
    }

    /**
     * Create a new song.
     *
     * @param id     Song id
     * @param title  Song title
     * @param artist Song artist
     * @param year   Song year
     * @param video  embed url
     * @param lyrics LRC lyrics
     */
    public Song(long id, String title, String artist, int year, String video, String lyrics) {
        this(title, artist, year, video, lyrics);
        this.id = id;
    }

    /**
     * Create a new song.
     *
     * @param id     song id
     * @param title  song title
     * @param artist artist
     * @param year   year
     * @param video  embed url
     */
    public Song(long id, String title, String artist, int year, String video) {
        this(title, artist, year, video);
        this.id = id;
    }

    /**
     * Copy constructor.
     *
     * @param s song
     */
    public Song(Song s) {
        this(s.id, s.title, s.artist, s.year, s.video, s.lyrics);
    }

    /**
     * Get the id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get title.
     *
     * @return song title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     *
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the artist name.
     *
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Set the artist.
     *
     * @param artist new artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Get release year.
     *
     * @return release year
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the release year.
     *
     * @param year new release year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Get the video.
     *
     * @return Video url
     */
    public String getVideo() {
        return video;
    }

    /**
     * Set the embed url.
     *
     * @param video URL
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * Get lyrics.
     *
     * @return song lyrics
     */
    public String getLyrics() {
        return lyrics;
    }

    /**
     * Set the lyrics.
     *
     * @param lyrics LRC lyrics
     */
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%d)", this.title, this.artist, this.year);
    }

}
