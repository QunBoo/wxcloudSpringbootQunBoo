package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.dto.UserDto;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User add(UserDto user) {
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        System.out.println("com.tencent.wxcloudrun.service.impl__Dto: " + user.toString());
        System.out.println("com.tencent.wxcloudrun.service.impl__User: " + newUser.toString());

        Integer lineId = userMapper.save(newUser);
        return newUser;
    }
}
