package uk.ac.ucl.rits.popchat.messages;

import javax.validation.constraints.NotNull;

/**
 * Request to change a user's password.
 *
 * @author RSDG
 *
 */
public class PasswordChange {

    @NotNull
    private String oldPassword, newPassword;

    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * @return the newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword the newPassword to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
