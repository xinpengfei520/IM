package com.xpf.im.model.bean;

/**
 * Created by xpf on 2016/11/7 :)
 * Wechat:18091383534
 * Function:
 */

public class PickContactInfo {

    public PickContactInfo() {
    }

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    private UserInfo user; // 联系人
    private boolean isChecked; // 是否被选中

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "PickContactInfo{" +
                "user=" + user +
                ", isChecked=" + isChecked +
                '}';
    }
}
