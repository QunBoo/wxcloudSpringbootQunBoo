package com.tencent.wxcloudrun.dto;
import lombok.Data;

@Data
public class UserDataDto {
    private String openId;
    private UserInfoDto userInfo;

    // openId的getter和setter
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    // userInfo的getter和setter
    public UserInfoDto getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDto userInfo) {
        this.userInfo = userInfo;
    }
}
