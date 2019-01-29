package uk.ac.ucl.rits.popchat.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A User represents a single individuals login credentials. It also stores what
 * access privileges they have.
 *
 * @author RSDG
 *
 */
@Entity
public class PopUser {

    @Id
    @Column(length = 50, unique = true, nullable = false)
    private String  username;

    @Column(length = 640, nullable = false)
    private String  password;

    private boolean isAdmin;

    /**
     * Create a new PopUser.
     */
    public PopUser() {}

    /**
     * Create a new PopUser.
     *
     * @param username The username
     * @param password The password (must be hashed if you want to save it to the
     *                 DB)
     * @param isAdmin  True if they are an admin user
     */
    public PopUser(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    /**
     * Get the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get if the user is an admin.
     *
     * @return is admin
     */
    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    /**
     * Set if the user is an admin.
     *
     * @param isAdmin admin state
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
