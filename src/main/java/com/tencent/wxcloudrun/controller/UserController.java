package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dto.UserDataDto;
import com.tencent.wxcloudrun.dto.UserDto;
import com.tencent.wxcloudrun.model.ResponseMessage;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @ApiOperation(value = "获取OpenId")
    @PostMapping("/getOpenId")
    public ResponseMessage<String> getOpenId(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        System.out.println("code: " + code);
//        String openId = userService.getOpenIdByCode(code);
        return ResponseMessage.success(code);
    }

    @ApiOperation(value = "保存用户信息")
    @PostMapping("/saveUser")
    public ResponseMessage<User> saveUser(@RequestBody UserDataDto userData) {
//        User savedUser = userService.save(userData);
        return ResponseMessage.success(userData);
    }


}
