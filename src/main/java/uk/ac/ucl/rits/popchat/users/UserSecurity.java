package uk.ac.ucl.rits.popchat.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

	@Column(length = 48, nullable = false)
	private byte[] salt;

	@Column(length = 50, nullable = false)
	private String algorithm;

	@NotNull
	private int iterations;

	@NotNull
	private int hashLength;

	public UserSecurity() {
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

	public boolean validatePassword(String password) {
		return false;
	}
	
	public static UserSecurity generateNewUser(String username, String password) {
		return null;
	}
}
