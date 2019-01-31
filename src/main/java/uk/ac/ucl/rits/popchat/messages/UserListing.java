package uk.ac.ucl.rits.popchat.messages;

/**
 * User with their admin status.
 *
 * @author RSDG
 *
 */
public class UserListing {

    private String  username;
    private boolean isAdmin;

    /**
     * Create a blank user listing.
     */
    public UserListing() {}

    /**
     * Create a new userlist.
     *
     * @param username Username
     * @param isAdmin If they are an admin
     */
    public UserListing(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the isAdmin
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
