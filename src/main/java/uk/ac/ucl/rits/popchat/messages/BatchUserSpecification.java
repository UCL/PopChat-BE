package uk.ac.ucl.rits.popchat.messages;

/**
 * This is the specification of the data required to batch create users.
 * These are a number of users to create, and a common username prefix.
 * 
 * @author RSDG
 *
 */
public class BatchUserSpecification {

	private int numUsers;
	private String prefix;

	public BatchUserSpecification() {
	}

	public int getNumUsers() {
		return numUsers;
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
