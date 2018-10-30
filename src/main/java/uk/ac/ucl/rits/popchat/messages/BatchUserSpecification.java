package uk.ac.ucl.rits.popchat.messages;

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
