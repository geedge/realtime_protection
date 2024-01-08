package com.realtime.protection.server.user.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {
    Integer login(@Param("username") String username, @Param("password") String password);
}
