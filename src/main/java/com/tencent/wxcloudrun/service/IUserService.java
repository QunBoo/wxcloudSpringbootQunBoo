package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.UserDto;
import com.tencent.wxcloudrun.model.User;

public interface IUserService {
    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return 添加后的用户信息
     */
    User add(UserDto user);
}
