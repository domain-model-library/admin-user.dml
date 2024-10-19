package dml.adminuser.entity;

import dml.keepalive.entity.AliveKeeperBase;

public class AdminUserSessionAliveKeeper extends AliveKeeperBase {

    private String sessionId;

    @Override
    public void setId(Object id) {
        this.sessionId = (String) id;
    }

    @Override
    public Object getId() {
        return sessionId;
    }
}
