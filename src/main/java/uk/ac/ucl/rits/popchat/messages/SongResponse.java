package uk.ac.ucl.rits.popchat.messages;

import uk.ac.ucl.rits.popchat.songs.Song;

public class SongResponse {

	private long id;
	private String title;
	private String artist;

	public SongResponse() {
	}

	public SongResponse(Song s) {
		this.id = s.getId();
		this.title = s.getTitle();
		this.artist = s.getArtist();
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

}
