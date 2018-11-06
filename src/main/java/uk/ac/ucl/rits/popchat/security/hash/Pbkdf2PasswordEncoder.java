package uk.ac.ucl.rits.popchat.security.hash;

import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${hash.length}")
	private int hashLength;

	@Value("${hash.iterations}")
	private int iterations;

	public Pbkdf2PasswordEncoder(String saltAlgorithm, int saltLength) throws NoSuchAlgorithmException {
		this.saltGen = RandomSalt.getSaltGenerator(saltAlgorithm);
		this.saltLength = saltLength;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			byte[] salt = this.saltGen.generateSalt(this.saltLength);
			char[] password = CharBuffer.wrap(rawPassword).array();
			return this.hashPassword(password, salt, this.iterations, this.hashLength);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("The salt specification is invalid. Please contact support");
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String[] parts = encodedPassword.split(":");
		char[] passChars = CharBuffer.wrap(rawPassword).array();
		byte[] salt = parts[1].getBytes();
		int iterations = Integer.parseInt(parts[2]);
		int hashLength = Integer.parseInt(parts[3]);
		String trialPassword = this.hashPassword(passChars, salt, iterations, hashLength);
		return MessageDigest.isEqual(encodedPassword.getBytes(), trialPassword.getBytes());
	}

	private String hashPassword(char[] password, byte[] salt, int iterations, int hashLength) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hashLength);
			SecretKeyFactory skf;
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			char[] hashChar = Hex.encode(hash);
			char[] saltChar = Hex.encode(salt);
			return String.format("%s:%s:%d:%d", hashChar, saltChar, iterations, hashLength);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("The password specification is invalid. Please contact support");
		}
	}
}
