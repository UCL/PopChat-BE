package uk.ac.ucl.rits.popchat.security.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import uk.ac.ucl.rits.popchat.security.salt.RandomSalt;
import uk.ac.ucl.rits.popchat.security.salt.SaltGenerator;

/**
 * A slow hasher that uses the PBKDR2 hash
 * 
 * @author RSDG
 *
 */
public class Pbkdf2PasswordEncoder implements PasswordEncoder {

	private final SaltGenerator saltGen;

	private final int saltLength;
	private final int hashLength;
	private final int iterations;

	/**
	 * Create a new Pdkdf2PassswordEncoder
	 * 
	 * @param saltAlgorithm  The algorithm to use for the salt
	 * @param saltLength     The length of the salt
	 * @param hashLength     The length of the generated hash
	 * @param hashIterations The number of iterations to run the hash for
	 * @throws NoSuchAlgorithmException If your algorithm of choice isn't available
	 */
	public Pbkdf2PasswordEncoder(String saltAlgorithm, int saltLength, int hashLength, int hashIterations)
			throws NoSuchAlgorithmException {
		this.saltGen = RandomSalt.getSaltGenerator(saltAlgorithm);
		this.saltLength = saltLength;
		this.hashLength = hashLength;
		this.iterations = hashIterations;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			byte[] salt = this.saltGen.generateSalt(this.saltLength);
			char[] password = rawPassword.toString().toCharArray();
			return this.hashPassword(password, salt, this.iterations, this.hashLength);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("The salt specification is invalid. Please contact support");
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String[] parts = encodedPassword.split(":");
		char[] passChars = rawPassword.toString().toCharArray();
		byte[] salt = Hex.decode(parts[1]);
		int iterations = Integer.parseInt(parts[2]);
		int hashLength = Integer.parseInt(parts[3]);
		String trialPassword = this.hashPassword(passChars, salt, iterations, hashLength);
		return MessageDigest.isEqual(encodedPassword.getBytes(), trialPassword.getBytes());
	}

	/**
	 * Hash the given password with the given salt
	 * 
	 * @param password   The password to hash
	 * @param salt       The salt to add to it
	 * @param iterations The number of iterations to hash for
	 * @param hashLength The length of the hash
	 * @return The hash concatenated with with its specification (salt, iterations,
	 *         and hashlength) with {@code :} separators
	 */
	private String hashPassword(char[] password, byte[] salt, int iterations, int hashLength) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hashLength);
			SecretKeyFactory skf;
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			char[] hashChar = Hex.encode(hash);
			char[] saltChar = Hex.encode(salt);
			return String.format("%s:%s:%d:%d", String.valueOf(hashChar), String.valueOf(saltChar), iterations,
					hashLength);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("The password specification is invalid. Please contact support");
		}
	}
}
