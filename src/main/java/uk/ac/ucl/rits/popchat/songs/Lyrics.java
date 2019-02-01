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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This represents a set of timed lyrics.
 *
 * @author RSDG
 *
 */
public class Lyrics {

    private final TreeMap<LocalTime, String> lyrics;

    /**
     * Create the Lyrics for a song.
     *
     * @param song Song to create lyrics for
     */
    public Lyrics(Song song) {
        lyrics = new TreeMap<>();
        this.parse(song.getLyrics());
    }

    /**
     * Create a lyrics from a list of timed lines.
     *
     * @param data Timed song lines
     */
    public Lyrics(Map.Entry<LocalTime, String>[] data) {
        lyrics = new TreeMap<>();
        for (Map.Entry<LocalTime, String> d : data) {
            lyrics.put(d.getKey(), d.getValue());
        }
    }

    /**
     * Parse the lyrics from the LRC string.
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
     * Get a segment of song of a given length. The max error to allow is 10% of the
     * requested total length
     *
     * @param targetLength The length of the requested segment
     * @return A shorter set of lyrics
     */
    public Lyrics getSegment(Duration targetLength) {
        return this.getSegment(targetLength, targetLength.dividedBy(10));
    }

    /**
     * Get a segment of the song that is approximately length in duration. The last
     * lyrics entry will be empty to make the segment as close to the right length
     * as possible.
     *
     * @param targetLength Length of the target song segment
     * @param maxMismatch  The maximum deviation in segment length
     * @return A Lyrics that is only the relevant part of the song. Null if no such
     *         segment can be found.
     */
    public Lyrics getSegment(Duration targetLength, Duration maxMismatch) {
        // This will find where `length` away from every lyric is.
        int[] segments = new int[this.lyrics.size()];
        // Initialise the array with -1 to indicate no match for any lyrics
        Arrays.fill(segments, -1);
        LocalTime[] durations = this.lyrics.keySet().toArray(new LocalTime[0]);

        // Length of each line
        int[] lengths = this.lyrics.values().stream().mapToInt(line -> line.strip().split("\\s+").length).toArray();

        // For each lyric, check to see if it is far enough way from the ones that came
        // before
        int numMatchedSegments = 0;
        for (int i = 1; i < this.lyrics.size(); i++) {
            LocalTime current = durations[i];
            // Look at the gap for all preceding lyrics
            for (int j = 0; j < i; j++) {
                LocalTime past = durations[j];
                // Get the time between the current lyrics
                Duration gap = Duration.between(past, current);

                // Get the difference between this gap, and how long we wanted to wait
                Duration mismatch = targetLength.minus(gap).abs();

                /// Get as close as possible
                if (segments[j] == -1 && mismatch.compareTo(maxMismatch) < 1) {
                    // This is the first match, so we care about it
                    numMatchedSegments++;
                    segments[j] = i;
                } else if (segments[j] > -1 && mismatch.compareTo(maxMismatch) < 1 && Duration
                        .between(past, durations[segments[j]]).minus(targetLength).abs().compareTo(mismatch) < 0) {
                    // This is a subsequent better match than the first found
                    segments[j] = i;
                }
            }
        }
        // Now Segments is an array of indexes for length second song extracts
        // From the valid keys, pick one
        if (numMatchedSegments == 0) {
            // There are none
            return null;
        }

        // Create weights for each segments based on the number of words
        int[] wordDensity = new int[lengths.length];
        int totalWordDensity = 0;
        for (int i = 0; i < segments.length; i++) {
            // They are zero, unless you have a match
            if (segments[i] >= 0) {
                // If you have a match it is the sum of all the lines
                // But not the last line, since that will be trimmed!
                for (int j = i; j < segments[i]; j++) {
                    wordDensity[i] += lengths[j];
                }
                totalWordDensity += wordDensity[i];
            }
        }

        // Pick a random segment based on word density
        int idx = (int) (Math.random() * totalWordDensity);
        for (int i = 0; i < segments.length; i++) {
            idx -= wordDensity[i];
            if (idx <= 0) {
                // This is the element you were looking for
                @SuppressWarnings("unchecked")
                Map.Entry<LocalTime, String>[] subSong = this.lyrics.entrySet().toArray(new Map.Entry[0]);
                Lyrics fragment = new Lyrics(Arrays.copyOfRange(subSong, i, segments[i] + 1));
                // Empty out the last line of lyrics. This makes this fragment as close as
                // possible
                // to the length it advertises itself to be, as times are to the start of the
                // line
                fragment.lyrics.put(fragment.lyrics.lastKey(), "");
                return fragment;
            }
        }
        throw new IllegalStateException("Randomly selected segment could not be recovered");
    }

    /**
     * Returns a proxy for the length of set of lyrics. Note that the length of the
     * lyrics is calculated as the time between the <b>start</b> of the first line
     * of lyrics and the <b>start</b> of the last line.
     *
     * @return Length of the song
     */
    public Duration getDuration() {
        return Duration.between(this.lyrics.firstKey(), this.lyrics.lastKey());
    }

    /**
     * Get the start time of this section of lyrics.
     *
     * @return Lyrics start time
     */
    public LocalTime getStartTime() {
        return this.lyrics.firstKey();
    }

    /**
     * Get the end time of this section of lyrics.
     *
     * @return Lyrics end time
     */
    public LocalTime getEndTime() {
        return this.lyrics.lastKey();
    }

    /**
     * Return the full text of the lyrics without timing information.
     *
     * @return The full song text
     */
    public String getText() {
        return this.lyrics.values().stream().map(line -> line + '\n').reduce(String::concat).orElse("");
    }

    /**
     * Return a list of all the words in the song with trailing and leading
     * punctuation removed. Note that this might cause problems with words like
     * {@code goin'} or {@code scammin'} since they will have the trailing {@code '}
     * in the rhyming dictionary.
     *
     * @return List of song words
     */
    public List<String> getWords() {
        return this.lyrics.values().stream()
                // Split lines into words
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                // Trim leading and trailing punctuation
                .map(word -> word.replaceAll("^[^a-zA-Z]+", "")).map(word -> word.replaceAll("[^a-zA-Z]+$", ""))
                .collect(Collectors.toList());
    }
}
