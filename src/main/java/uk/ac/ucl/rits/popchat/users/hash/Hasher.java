package uk.ac.ucl.rits.popchat.users.hash;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * A Hasher applies a slow hash to a password
 * 
 * @author RSDG
 *
 */
public interface Hasher {

	/**
	 * Apply a slow hash to the password
	 * 
	 * @param password   The password text
	 * @param salt       The random salt
	 * @param iterations Number of iterations to run the hash for
	 * @param size       The length of the output
	 * @return The hashed password
	 * @throws NoSuchAlgorithmException If the runtime does not support the hashing
	 *                                  algorithm
	 * @throws InvalidKeySpecException  If the combination of options is not valid
	 */
	public String applyHash(char[] password, byte[] salt, int iterations, int size)
			throws NoSuchAlgorithmException, InvalidKeySpecException;

	/**
	 * Pretty print a byte array as a hex string with leading zeros
	 * 
	 * @param array The bytes to print
	 * @return Hex string with leading zeros
	 */
	public default String toHex(byte[] array) {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d%s", 0, hex);
		} else {
			return hex;
		}
	}
}
