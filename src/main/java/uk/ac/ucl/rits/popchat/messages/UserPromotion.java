package uk.ac.ucl.rits.popchat.messages;

/**
 * Message class for user promotion and demotion messages. If promote is true it
 * makes a user an admin. Otherwise it removes admin privilages.
 *
 * @author RSDG
 *
 */
public class UserPromotion {
    private String  username;
    private boolean promote;

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
     * @return the promote
     */
    public boolean isPromote() {
        return promote;
    }

    /**
     * @param promote the promote to set
     */
    public void setPromote(boolean promote) {
        this.promote = promote;
    }

}
