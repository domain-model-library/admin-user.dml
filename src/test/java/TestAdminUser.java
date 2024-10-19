import dml.adminuser.entity.AdminUserBase;

public class TestAdminUser extends AdminUserBase {
    private String account;

    @Override
    public void setAccount(String account) {
        this.account = account;
    }
    
}
