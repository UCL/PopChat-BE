package uk.ac.ucl.rits.popchat.users.salt;

import java.security.NoSuchAlgorithmException;

/**
 * A class to generate random salts
 * 
 * @author RSDG
 *
 */
public interface SaltGenerator {

	/**
	 * Generate a random salt of specified length
	 * 
	 * @param length The length of the salt
	 * @return A the bytes of the salt
	 * @throws NoSuchAlgorithmException If the runtime fails to find the algorithm
	 */
	public byte[] generateSalt(int length) throws NoSuchAlgorithmException;
}
