import dml.adminuser.entity.AdminUserSession;

public class TestAdminUserSession implements AdminUserSession {
    private String id;
    private String account;

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAccount() {
        return account;
    }


}
