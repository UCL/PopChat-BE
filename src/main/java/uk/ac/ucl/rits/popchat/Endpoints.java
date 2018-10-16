package uk.ac.ucl.rits.popchat;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;

@RestController
public class Endpoints {

	private static final Logger log = LoggerFactory.getLogger(Endpoints.class);

	@RequestMapping("words/rhymes-with/{word}")
	public Set<String> rhymesWith(@PathVariable String word) {
		log.trace(String.format("Finding rhymes for %s", word));
		return Rhymes.getRhymes().rhymes(word.toLowerCase());
	}

}