package dml.adminuser.service.result;

import dml.adminuser.entity.AdminUserSession;

public class LoginResult {
    private boolean success;
    private boolean noAccount;
    private boolean wrongPassword;
    private boolean banned;
    private AdminUserSession removedSession;
    private AdminUserSession newSession;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isNoAccount() {
        return noAccount;
    }

    public void setNoAccount(boolean noAccount) {
        this.noAccount = noAccount;
    }

    public boolean isWrongPassword() {
        return wrongPassword;
    }

    public void setWrongPassword(boolean wrongPassword) {
        this.wrongPassword = wrongPassword;
    }

    public AdminUserSession getRemovedSession() {
        return removedSession;
    }

    public void setRemovedSession(AdminUserSession removedSession) {
        this.removedSession = removedSession;
    }

    public AdminUserSession getNewSession() {
        return newSession;
    }

    public void setNewSession(AdminUserSession newSession) {
        this.newSession = newSession;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
