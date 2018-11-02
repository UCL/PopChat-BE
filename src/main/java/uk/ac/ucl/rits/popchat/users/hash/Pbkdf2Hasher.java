package uk.ac.ucl.rits.popchat.users.hash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A slow hasher that uses the PBKDR2 hash
 * 
 * @author RSDG
 *
 */
public class Pbkdf2Hasher implements Hasher {

	public String applyHash(char[] password, byte[] salt, int iterations, int size)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 64 * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return this.toHex(hash);
	}
}
