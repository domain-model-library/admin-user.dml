package dml.adminuser.service.result;

import dml.adminuser.entity.AdminUser;

public class AddAdminUserResult {
    private boolean success;
    private boolean alreadyExist;
    private AdminUser newAdminUser;
    private AdminUser existAdminUser;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isAlreadyExist() {
        return alreadyExist;
    }

    public void setAlreadyExist(boolean alreadyExist) {
        this.alreadyExist = alreadyExist;
    }

    public AdminUser getNewAdminUser() {
        return newAdminUser;
    }

    public void setNewAdminUser(AdminUser newAdminUser) {
        this.newAdminUser = newAdminUser;
    }

    public AdminUser getExistAdminUser() {
        return existAdminUser;
    }

    public void setExistAdminUser(AdminUser existAdminUser) {
        this.existAdminUser = existAdminUser;
    }
}
