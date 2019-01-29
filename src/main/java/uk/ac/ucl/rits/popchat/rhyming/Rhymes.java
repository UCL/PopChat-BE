package uk.ac.ucl.rits.popchat.rhyming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.ucl.rits.popchat.songs.Lyrics;

/**
 * This class allows maps known words to sets of words that rhyme with it. Note
 * that it will on detect perfect rhymes.
 */
public class Rhymes {

    /** Location of the file containing word to pronunciation mappings. */
    private final String                   dictfile = "/cmudict-0.7b";

    /** List of all known words with their pronunciations. */
    private final List<Word>               pronunciations;
    /** Maps a word to all of its pronunciations. */
    private final Map<String, Set<String>> lookup;
    /** Gets all words that can have the same end sound. */
    private final Map<String, Set<String>> rhymeLookup;

    /** Instance of Rhymes used for the singleton pattern. */
    private static Rhymes                  me       = null;

    /**
     * Create a new Rhymes instance. Note that this reads the relevant files and
     * precomputes all the necessary relationships when called.
     */
    private Rhymes() {
        this.pronunciations = parseCmu();
        this.lookup = listToMultimap(pronunciations);
        this.rhymeLookup = initRhymeMap(pronunciations);
    }

    /**
     * Open the CMU language dictionary and read every word to pronunciation pair.
     *
     * @return List of all words in the CMU dictionary
     */
    private List<Word> parseCmu() {
        // Open the file
        try (InputStream in = Rhymes.class.getResourceAsStream(this.dictfile);
                Reader r = new InputStreamReader(in, StandardCharsets.ISO_8859_1);
                BufferedReader br = new BufferedReader(r);
                Stream<String> lines = br.lines();) {
            // Read line by line
            return lines
                    // Drop comment lines
                    .filter((line) -> !line.startsWith(";"))
                    // Split by the word pronunciation separator " " (" " separates sounds)
                    .map(line -> line.trim().split("  "))
                    // Each line must have exactly 2 parts
                    .map((data) -> {
                        if (data.length != 2) {
                            throw new IllegalStateException("Each row must contain exactly 2 elements");
                        }
                        return data;
                    })
                    // Lower case the words, and remove the numbers (what number definition it is)
                    .map(data -> new Word(data[0].replaceAll("\\([0-9]+\\)$", "").toLowerCase(), data[1]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    /**
     * Take a list of words, and create a map from rhyming endings to all possible
     * words.
     *
     * @param list List of words to map
     * @return A map from rhyming parts to rhyming words
     */
    private Map<String, Set<String>> initRhymeMap(List<Word> list) {
        Map<String, Set<String>> wordMap = new HashMap<>();
        list.forEach(word -> {
            String key = rhymingPart(word.phones);
            if (wordMap.containsKey(key)) {
                Set<String> matchedWords = wordMap.get(key);
                matchedWords.add(word.word);
            } else {
                Set<String> matchedWords = new HashSet<>();
                matchedWords.add(word.word);
                wordMap.put(key, matchedWords);
            }
        });

        return wordMap;
    }

    /**
     * For each word, map it to a set of all of its pronunciations.
     *
     * @param list A List of words and pronunciations where each word may appear
     *             more than once
     * @return A Map keyed on words, going to the set of possible pronunciations
     */
    private Map<String, Set<String>> listToMultimap(List<Word> list) {
        Map<String, Set<String>> wordMap = new HashMap<>();
        list.forEach(word -> {
            Set<String> phones = wordMap.get(word.word);
            if (phones == null) {
                phones = new HashSet<String>();
                wordMap.put(word.word, phones);
            }
            phones.add(word.phones);
        });
        return wordMap;
    }

    /**
     * Get the ending part of the word, ie the part involved in the rhyme.
     *
     * @param phones All of the sounds making up the word. Each separated by a " "
     * @return The sounds making up only the rhyming bit (separated by " ")
     */
    private String rhymingPart(String phones) {
        String[] phonesList = phones.split(" ");
        int rhymingStart = -1;
        for (int i = 0; i < phonesList.length; i++) {
            String phone = phonesList[i];
            if (phone.endsWith("1") || phone.endsWith("2")) {
                rhymingStart = i;
            }
        }
        if (rhymingStart == -1) {
            // The whole word is involved in the rhyme
            return phones;
        }
        // Turn the relevant phones back into a single string
        StringBuilder builder = new StringBuilder();
        for (int i = rhymingStart; i < phonesList.length; i++) {
            builder.append(phonesList[i]);
            if (i != phonesList.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * Get the possible pronunciations of a word.
     *
     * @param lookup Map of all pronunciations of all known words
     * @param word   Word to look up
     * @return A set of all possible pronunciations
     */
    private Set<String> getPhonesForWord(Map<String, Set<String>> lookup, String word) {
        Set<String> words = lookup.get(word);
        if (words == null) {
            return new HashSet<>();
        }
        return words;
    }

    /**
     * Find the words that rhyme with a given word.
     *
     * @param word The word to find rhymes for
     * @return All the known words that rhyme with it.
     */
    public Set<String> rhymes(String word) {
        final String target = word.toLowerCase();
        return getPhonesForWord(lookup, target).stream().map(w -> rhymingPart(w))
                .flatMap(w -> this.rhymeLookup.get(w).stream()).filter(w -> !w.equals(target))
                .collect(Collectors.toSet());
    }

    /**
     * Given a song fragment find a set of mutually rhyming words.
     *
     * @param fragment Song fragment to pick words from
     * @return Set of words from the fragment which all should rhyme
     */
    public Set<String> createRhymesWithGame(Lyrics fragment) {
        Set<String> allWords = new HashSet<>(fragment.getWords());
        Set<String> seenWords = new HashSet<>();
        Set<Set<String>> rhymes = new HashSet<>();

        // Find the equivalence classes of rhyming words in the fragment
        for (String word : allWords) {
            if (seenWords.contains(word)) {
                continue;
            }
            Set<String> matches = this.rhymes(word);
            matches.retainAll(allWords);
            if (matches.isEmpty()) {
                seenWords.add(word);
            } else {
                matches.add(word);
                rhymes.add(matches);
                seenWords.addAll(matches);
            }
        }
        int totalWords = rhymes.stream().mapToInt(e -> e.size()).sum();
        if (totalWords == 0) {
            // There are no rhyming matches in this set.
            return new HashSet<String>();
        }
        // Randomly pick based on word density
        int pick = (int) (Math.random() * totalWords);
        for (Set<String> words : rhymes) {
            pick -= words.size();
            if (pick <= 0) {
                return words;
            }
        }
        throw new IllegalStateException("Failed to select random set of rhyming words");
    }

    /**
     * Get an instance of Rhymes that you can use.
     *
     * @return An instance of Rhymes
     */
    public static Rhymes getRhymes() {
        if (me == null) {
            me = new Rhymes();
        }
        return me;
    }

}
