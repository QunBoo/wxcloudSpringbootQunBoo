package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dto.UserDto;
import com.tencent.wxcloudrun.model.ResponseMessage;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    public UserController() {
        System.out.println("UserController 已初始化");
    }

    @GetMapping
    public String getUsers() {
        return "This is user endpoint";
    }

    @PostMapping
    public ResponseMessage<User> add(@RequestBody UserDto user) {
//        User u = new User();
        System.out.println("com.tencent.wxcloudrun.controller__Dto:" + user.toString());
        User u = userService.add(user);
        System.out.println("com.tencent.wxcloudrun.controller: " + u.toString());
        return ResponseMessage.success(u);
    }
}
