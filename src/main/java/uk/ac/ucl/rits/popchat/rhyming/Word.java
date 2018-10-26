package uk.ac.ucl.rits.popchat.rhyming;

/**
 * A word represents both a word (its spelling) and one possible pronunciation.
 * 
 * @author RSDG
 *
 */
public class Word {

	/**
	 * The word (spelling, lowercase)
	 */
	public final String word;
	
	/**
	 * The pronuntiation (arpabet)
	 */
	public final String phones;

	/**
	 * Create a new word
	 * @param word Lowercase word
	 * @param phones The arpabet pronunctiation of the word
	 */
	public Word(String word, String phones) {
		this.word = word;
		this.phones = phones;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", word, phones);
	}
}
