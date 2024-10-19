package dml.adminuser.entity;

/**
 * @author zheng chengdong
 */
public class AdminUserCurrentSession {
    private String account;
    private String currentSessionId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void setCurrentSessionId(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }
}
