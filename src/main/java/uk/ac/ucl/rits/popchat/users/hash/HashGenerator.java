package uk.ac.ucl.rits.popchat.users.hash;

import java.security.NoSuchAlgorithmException;

/**
 * Factory to get Hashers
 * 
 * @author RSDG
 *
 */
public class HashGenerator {

	public static final String PBKDF2 = "PBFDF2";

	/**
	 * You cannot create a HashGenerator
	 */
	private HashGenerator() {
	}

	public static Hasher getHasher(String strategy) throws NoSuchAlgorithmException {
		switch (strategy) {
		case HashGenerator.PBKDF2:
			return new Pbkdf2Hasher();
		default:
			throw new NoSuchAlgorithmException(
					String.format("The algorithm %s is not available for hash generation", strategy));
		}
	}

}
