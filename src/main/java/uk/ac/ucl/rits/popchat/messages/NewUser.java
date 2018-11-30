package uk.ac.ucl.rits.popchat.messages;

/**
 * This class represents what information specifies a new user account to
 * create. All such accounts are non-administrative.
 * 
 * @author RSDG
 *
 */
public class NewUser {

	private String username;
	private String password;

	public NewUser() {
	}

	public NewUser(String username, String password) {
		this.username = username;
		this.password = password;
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

}
