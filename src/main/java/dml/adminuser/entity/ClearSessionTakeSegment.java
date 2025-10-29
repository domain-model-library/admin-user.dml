package dml.adminuser.entity;

import dml.largescaletaskmanagement.entity.LargeScaleTaskSegmentBase;

public class ClearSessionTakeSegment extends LargeScaleTaskSegmentBase {

    private String sessionId;

    public void setId(Object id) {
        this.sessionId = (String) id;
    }

    @Override
    public Object getId() {
        return sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
