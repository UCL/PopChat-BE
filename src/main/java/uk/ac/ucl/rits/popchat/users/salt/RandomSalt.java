package uk.ac.ucl.rits.popchat.users.salt;

import java.security.NoSuchAlgorithmException;

/**
 * Generate a generator to create random salts
 * 
 * @author RSDG
 *
 */
public class RandomSalt {

	public static final String SALT_SHA1PRNG = "SHA1PRNG";

	/**
	 * You cannot create an instance of this class
	 */
	private RandomSalt() {
	}

	/**
	 * Generate a random salt generator. The available options (subject to system
	 * availability) are the static SALT_ strings of this class
	 * 
	 * @param strategy The strategy for the salt generator to use
	 * @return A SaltGenerator
	 * @throws NoSuchAlgorithmException If the system cannot find the strategy
	 */
	public static SaltGenerator getSaltGenerator(String strategy) throws NoSuchAlgorithmException {
		switch (strategy) {
		case RandomSalt.SALT_SHA1PRNG:
			return new Sha1prngSaltGenerator();
		default:
			throw new NoSuchAlgorithmException(
					String.format("The algorithm %s is not available for salt generation", strategy));
		}
	}

}
