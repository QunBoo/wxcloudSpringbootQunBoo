package com.tencent.wxcloudrun.dto;
import lombok.Data;

@Data
public class UserInfoDto {
    private String nickName;
    private String avatarUrl;
    private Integer gender;
    private String city;
    private String province;
    private String country;
    private String language;
}