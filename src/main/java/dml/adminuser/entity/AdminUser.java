package dml.adminuser.entity;

public interface AdminUser {
    void setAccount(String account);

    void setPassword(String password);

    boolean verifyPassword(String password);

    void setBanned(boolean banned);

    boolean isBanned();
}
