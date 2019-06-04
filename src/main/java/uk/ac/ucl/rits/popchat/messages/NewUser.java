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

    /**
     * Create a new user.
     */
    public NewUser() {}

    /**
     * Create a new user.
     *
     * @param username The username
     * @param password The raw password
     */
    public NewUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param username username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password.
     *
     * @return Raw password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the raw password.
     *
     * @param password password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
