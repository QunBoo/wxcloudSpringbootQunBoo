package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dto.UserDto;
import com.tencent.wxcloudrun.model.ResponseMessage;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户管理功能")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    public UserController() {
        System.out.println("UserController 已初始化");
    }

    @ApiOperation(value = "获取用户列表")
    @GetMapping
    public String getUsers() {
        return "This is user endpoint";
    }

    @ApiOperation(value = "添加用户")
    @PostMapping
    public ResponseMessage<User> add(@RequestBody UserDto user) {
//        User u = new User();
        System.out.println("com.tencent.wxcloudrun.controller__Dto:" + user.toString());
        User u = userService.add(user);
        System.out.println("com.tencent.wxcloudrun.controller: " + u.toString());
        return ResponseMessage.success(u);
    }
}
