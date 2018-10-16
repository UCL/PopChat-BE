package uk.ac.ucl.rits.popchat.rhyming;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class RhymesTest {

	@Test
	public void testRhymes() {
		Rhymes rhymes = Rhymes.getRhymes();

		assertNotNull(rhymes);

		String word = "Raquel";
		Set<String> matches = rhymes.rhymes(word);
		assertTrue(matches.size() > 0);
		assertFalse(matches.contains(word));
		assertTrue(matches.contains("michelle"));
	}
}
