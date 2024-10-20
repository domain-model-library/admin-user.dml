package dml.adminuser.entity;

import dml.largescaletaskmanagement.entity.LargeScaleTaskBase;

public class ClearSessionTask extends LargeScaleTaskBase {
    private String taskName;

    @Override
    public void setName(String name) {
        this.taskName = name;
    }
}
