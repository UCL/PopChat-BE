package uk.ac.ucl.rits.popchat.security.hash;

import java.math.BigInteger;

/**
 * A Hasher applies a slow hash to a password
 * 
 * @author RSDG
 *
 */
public class HashUtil {

	public final static String PBKDF2 = "PBKDF2";

	/**
	 * Pretty print a byte array as a hex string with leading zeros
	 * 
	 * @param array The bytes to print
	 * @return Hex string with leading zeros
	 */
	public static String toHex(byte[] array) {
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
