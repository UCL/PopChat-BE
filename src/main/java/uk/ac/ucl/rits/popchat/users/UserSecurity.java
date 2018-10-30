package uk.ac.ucl.rits.popchat.users;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import uk.ac.ucl.rits.popchat.users.hash.HashGenerator;
import uk.ac.ucl.rits.popchat.users.hash.Hasher;
import uk.ac.ucl.rits.popchat.users.salt.RandomSalt;
import uk.ac.ucl.rits.popchat.users.salt.SaltGenerator;

/**
 * A User represents a single individuals login credentials.
 * 
 * @author RSDG
 *
 */
@Entity
@Table(indexes = { @Index(columnList = ("username"), name = "username_index") })
public class UserSecurity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 50, unique = true, nullable = false)
	private String username;

	@Column(length = 512, nullable = false)
	private String password;

	@Column(length = 32, nullable = false)
	private byte[] salt;

	@Column(length = 50, nullable = false)
	private String algorithm;

	@NotNull
	private int iterations;

	@NotNull
	private int hashLength;

	public UserSecurity() {
	}

	public UserSecurity(String username, String password, byte[] salt, String hashAlgorithm, int iterations,
			int hashLength) {
		this.username = username;
		this.password = password;
		this.salt = salt.clone();
		this.algorithm = hashAlgorithm;
		this.iterations = iterations;
		this.hashLength = hashLength;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getHashLength() {
		return hashLength;
	}

	public void setHashLength(int hashLength) {
		this.hashLength = hashLength;
	}

	public boolean validatePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Hasher hasher = HashGenerator.getHasher(this.algorithm);
		String hash = hasher.applyHash(password.toCharArray(), this.salt, this.iterations, this.hashLength);
		return hash.equals(this.password);
	}

	public static UserSecurity generateNewUser(String username, String password, String hashAlgorithm,
			String saltAlgorithm, int iterations, int saltLength, int hashLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SaltGenerator saltGen = RandomSalt.getSaltGenerator(saltAlgorithm);
		byte[] salt = saltGen.generateSalt(saltLength);
		Hasher hasher = HashGenerator.getHasher(hashAlgorithm);
		String hash = hasher.applyHash(password.toCharArray(), salt, iterations, hashLength);
		return new UserSecurity(username, hash, salt, hashAlgorithm, iterations, hashLength);
	}
}
