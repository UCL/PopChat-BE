package uk.ac.ucl.rits.popchat.songs;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This represents a set of timed lyrics.
 * 
 * @author RSDG
 *
 */
public class Lyrics {

	private final Map<Duration, String> lyrics;

	/**
	 * Create the Lyrics for a song
	 * 
	 * @param song Song to create lyrics for
	 */
	public Lyrics(Song song) {
		lyrics = new TreeMap<>();
		this.parse(song.getLyrics());
	}

	/**
	 * Parse the lyrics from the LRC string
	 * 
	 * @param lyrics The LRC string of the lyrics
	 */
	private void parse(String lyrics) {
		lyrics.lines().forEachOrdered(line -> {
			if (line.startsWith("[")) {
				String[] parts = line.split("]");
				if (parts.length == 2) {
					// Trim the starting [
					String right = parts[0].substring(1);
					String lyric = parts[1];
					Duration time = Duration.parse(right);
					this.lyrics.put(time, lyric);
				}
				// If it isn't two, it is metadata, or some other kind of message
			}
			// All LRC lines must start with [, so anything else is an error
		});
	}

	/**
	 * Get the line of lyrics that will be playing at a particular time. This does
	 * not account for silence. It is the last line that would have appeared, or the
	 * first line to come (if the time is before singing starts).
	 * 
	 * @param d Time to get the lyrics for
	 * @return The lyrics to show. Null if there are no lyrics.
	 */
	public String lyricsAt(Duration d) {
		for (Iterator<Map.Entry<Duration, String>> i = lyrics.entrySet().iterator(); i.hasNext();) {
			Map.Entry<Duration, String> e = i.next();
			Duration end = e.getKey();
			// Iteration through a tree map is temporally ordered,
			// So you know that you have looked at all the earlier times already
			if (d.compareTo(end) < 0 || !i.hasNext()) {
				return e.getValue();
			}
		}
		// The list was empty, return null
		return null;
	}

	/**
	 * Get a segment of the song that is approximately length in duration.
	 * 
	 * @param length Length of the target song segment
	 */
	public void getSegment(Duration targetLength) {
		// This will find where `length` away from every lyric is.
		int[] segments = new int[this.lyrics.size()];
		// Initialise the array with -1 to indicate no match for any lyrics
		Arrays.fill(segments, -1);
		Duration[] durations = this.lyrics.keySet().toArray(new Duration[0]);
		// For each lyric, check to see if it is far enough way from the ones that came
		// before

		// Should this be proportional to the length of the segment?
		Duration maxMismatch = Duration.ofSeconds(10);

		for (int i = 1; i < this.lyrics.size(); i++) {
			Duration current = durations[i];
			// Look at the gap for all preceding lyrics
			for (int j = 0; j < i; j++) {
				if (segments[j] >= 0) {
					// If we have already matched an end, skip the lyric
					continue;
				}
				Duration past = durations[j];
				// Get the time between the current lyrics
				Duration gap = current.minus(past);

				// Get the difference between this gap, and how long we wanted to wait
				Duration mismatch = targetLength.minus(gap).abs();

				/// TODO better to overshoot than undershoot?
				if (mismatch.compareTo(maxMismatch) < 1) {
					segments[i] = j;
				}
			}
		}
		// TODO: If I didn't find anythings should I try again but being more lenient?
		// Now Segments is an array of indexes for length second song extracts
	}

}
