package uk.ac.ucl.rits.popchat.messages;

/**
 * This is the specification of the data required to batch create users. These
 * are a number of users to create, and a common username prefix.
 *
 * @author RSDG
 *
 */
public class BatchUserSpecification {

    private int    numUsers;
    private String prefix;

    /**
     * Create a specification of a batch user.
     */
    public BatchUserSpecification() {}

    /**
     * Get the number of users to create.
     *
     * @return The number of accounts to create
     */
    public int getNumUsers() {
        return numUsers;
    }

    /**
     * Set the number of accounts to create.
     *
     * @param numUsers Accounts to create
     */
    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    /**
     * Get the prefix that all usernames should start with.
     *
     * @return Common username prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the username prefix.
     *
     * @param prefix Username prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
