package dml.adminuser.entity;

public abstract class AdminUserBase implements AdminUser {

    protected String password;
    protected boolean banned;

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public boolean isBanned() {
        return banned;
    }
}
