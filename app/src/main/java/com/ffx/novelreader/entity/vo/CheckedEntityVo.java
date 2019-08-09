package com.ffx.novelreader.entity.vo;

/**
 * Created by TwoFlyLiu on 2019/8/8.
 */

public class CheckedEntityVo<Entity> {
    private Entity entity;
    private boolean checked;

    public CheckedEntityVo() {}

    public CheckedEntityVo(Entity entity, boolean checked) {
        this.entity = entity;
        this.checked = checked;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
