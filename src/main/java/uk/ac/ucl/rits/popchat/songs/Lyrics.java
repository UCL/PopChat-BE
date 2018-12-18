package uk.ac.ucl.rits.popchat.songs;

import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
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

	private final TreeMap<LocalTime, String> lyrics;

	/**
	 * Create the Lyrics for a song
	 * 
	 * @param song Song to create lyrics for
	 */
	public Lyrics(Song song) {
		lyrics = new TreeMap<>();
		this.parse(song.getLyrics());
	}

	public Lyrics(Map.Entry<LocalTime, String>[] data) {
		lyrics = new TreeMap<>();
		for (Map.Entry<LocalTime, String> d : data) {
			lyrics.put(d.getKey(), d.getValue());
		}
	}

	/**
	 * Parse the lyrics from the LRC string
	 * 
	 * @param lyrics The LRC string of the lyrics
	 */
	private void parse(String lyrics) {
		DateTimeFormatter format = new DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient().optionalStart()
				.appendValue(MINUTE_OF_DAY, 2).appendLiteral(':').optionalEnd().appendValue(SECOND_OF_MINUTE, 2)
				.optionalStart().appendFraction(NANO_OF_SECOND, 0, 9, true).optionalEnd().toFormatter()
				.withZone(ZoneOffset.UTC);

		lyrics.lines().map(line -> line.trim())
				// Only read lines that are time stamp lines
				.filter(line -> {
					return !line.endsWith("]");
				}).forEachOrdered(line -> {
					if (line.startsWith("[")) {
						String[] parts = line.split("]");
						if (parts.length > 1) {
							// Trim the starting [
							String right = parts[0].substring(1);
							String lyric = parts[parts.length - 1];

							TemporalAccessor temp = format.parse(right);
							LocalTime time = LocalTime.from(temp);
							this.lyrics.put(time, lyric);
						}
						// If it isn't two, it is metadata, or some other kind of message
					}
					// All LRC lines must start with [, so anything else is an error
				});
	}
	
	/**
	 * Get the last line of lyrics that was marked to start before a particular
	 * time, but before the next line has started. This does not account for
	 * silence. It is the last line that would have appeared. If the time is before
	 * the start of the first lyrics it will return null.
	 * 
	 * @param d Time to get the lyrics for
	 * @return The lyrics to show. Null if there are no lyrics.
	 */
	public String lyricsAt(LocalTime d) {
		Map.Entry<LocalTime, String> last = null;
		for (Iterator<Map.Entry<LocalTime, String>> i = lyrics.entrySet().iterator(); i.hasNext();) {
			Map.Entry<LocalTime, String> e = i.next();
			LocalTime end = e.getKey();
			// Iteration through a tree map is temporally ordered,
			// So you know that you have looked at all the earlier times already
			if (d.compareTo(end) < 0) {
				return last == null ? null : last.getValue();
			}
			last = e;
		}
		// The list was empty, return null
		return last == null ? null : last.getValue();
	}
	
	
	/**
	 * Get a segment of song of a given length.
	 * The max error to allow is 10% of the requested total length
	 * 
	 * @param targetLength The length of the requested segment
	 * @return A shorter set of lyrics
	 */
	public Lyrics getSegment(Duration targetLength) {
		return this.getSegment(targetLength, targetLength.dividedBy(10));
	}

	/**
	 * Get a segment of the song that is approximately length in duration.
	 * 
	 * @param length Length of the target song segment
	 * @param error The maximum deviation in segment length
	 * @return A Lyrics that is only the relevant part of the song. Null if no such
	 *         segment can be found.
	 */
	public Lyrics getSegment(Duration targetLength, Duration maxMismatch) {
		// This will find where `length` away from every lyric is.
		int[] segments = new int[this.lyrics.size()];
		// Initialise the array with -1 to indicate no match for any lyrics
		Arrays.fill(segments, -1);
		LocalTime[] durations = this.lyrics.keySet().toArray(new LocalTime[0]);
		// For each lyric, check to see if it is far enough way from the ones that came
		// before

		int numMatchedSegments = 0;
		for (int i = 1; i < this.lyrics.size(); i++) {
			LocalTime current = durations[i];
			// Look at the gap for all preceding lyrics
			for (int j = 0; j < i; j++) {
				if (segments[j] >= 0) {
					// If we have already matched an end, skip the lyric
					continue;
				}
				LocalTime past = durations[j];
				// Get the time between the current lyrics
				Duration gap = Duration.between(past, current);

				// Get the difference between this gap, and how long we wanted to wait
				Duration mismatch = targetLength.minus(gap).abs();

				/// TODO better to overshoot than under shoot?
				if (mismatch.compareTo(maxMismatch) < 1) {
					numMatchedSegments++;
					segments[j] = i;
				}
			}
		}
		// TODO: If I didn't find anythings should I try again but being more lenient?
		// Now Segments is an array of indexes for length second song extracts
		// From the valid keys, pick one
		if (numMatchedSegments == 0) {
			// There are none
			return null;
		}
		// Pick a random segment
		int idx = (int) (Math.random() * numMatchedSegments);
		for (int i = 0; i < segments.length; i++) {
			if (segments[i] != 0) {
				idx--;
			}
			if (idx == 0) {
				// This is the element you were looking for
				@SuppressWarnings("unchecked")
				Map.Entry<LocalTime, String>[] subSong = this.lyrics.entrySet().toArray(new Map.Entry[0]);
				return new Lyrics(Arrays.copyOfRange(subSong, i, segments[i] + 1));
			}
		}
		throw new IllegalStateException("Randomly selected segment could not be recovered");
	}

	/**
	 * Returns the length of set of lyrics.
	 * Note that the length of the lyrics is calculated as the time between the
	 * <b>start</b> of the first line of lyrics and the <b>start</b> of the last line. 
	 * 
	 * @return Length of the song
	 */
	public Duration getDuration() {
		return Duration.between(this.lyrics.firstKey(), this.lyrics.lastKey());
	}

}
