package uk.ac.ucl.rits.popchat;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;

/**
 * This class controls endpoints related to words and their relationships to
 * each other
 * 
 * @author RSDG
 *
 */
@RestController
public class WordEndpoints {

	private static final Logger log = LoggerFactory.getLogger(WordEndpoints.class);

	/**
	 * Find all the words in the dictionary that have a perfect rhyme with the given
	 * word
	 * 
	 * @param word The word to rhyme with
	 * @return All known words that rhyme with the given word (excluding itself)
	 */
	@GetMapping("words/rhymes-with/{word}")
	public Set<String> rhymesWith(@PathVariable String word) {
		log.trace(String.format("Finding rhymes for %s", word));
		return Rhymes.getRhymes().rhymes(word.toLowerCase());
	}

}