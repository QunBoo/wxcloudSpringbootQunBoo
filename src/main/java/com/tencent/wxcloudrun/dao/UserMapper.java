package com.tencent.wxcloudrun.dao;
import com.tencent.wxcloudrun.model.User;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 插入用户
     * @param user 用户对象
     * @return 插入的行数
     */
    @Insert("insert into tb_user (username, password, email) values (#{username}, #{password}, #{email})")
    public int save(User user);
}
