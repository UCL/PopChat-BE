package uk.ac.ucl.rits.popchat.songs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Song {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String title;
	private String artist;
	private int year;
	private String video;

	@Column(columnDefinition = "TEXT")
	private String lyrics;

	public Song() {
	}

	public Song(String title, String artist, int year, String video) {
		this(title, artist, year, video, null);
	}

	public Song(String title, String artist, int year, String video, String lyrics) {
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.video = video;
		this.lyrics = lyrics;
	}

	public Song(long id, String title, String artist, int year, String video, String lyrics) {
		this(title, artist, year, video, lyrics);
		this.id = id;
	}

	public Song(long id, String title, String artist, int year, String video) {
		this(title, artist, year, video);
		this.id = id;
	}

	public Song(Song s) {
		this(s.id, s.title, s.artist, s.year, s.video, s.lyrics);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	@Override
	public String toString() {
		return String.format("%s: %s (%d)", this.title, this.artist, this.year);
	}

}
