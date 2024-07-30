package com.realtime.protection.server.user;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.server.user.login.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class LoginServiceTest extends ProtectionApplicationTests {

    private final LoginService loginService;

    @Autowired
    LoginServiceTest(LoginService loginService) {
        this.loginService = loginService;
    }


    @Test
    void testStoreUserFullToRedis() {
        // test code here

        Map<String, String> userRedisMap = new HashMap<>();
        userRedisMap.put("sessionData", "sessionData22222222222222222222222222222222222222222222");
        userRedisMap.put("userId", "111111");
        userRedisMap.put("userName", "张三");
        userRedisMap.put("userRole", "admin");
        userRedisMap.put("UserDepartmentName", "二处");
        userRedisMap.put("UserDepartmentCode", "2");


        assert loginService.storeUserFullToRedis(userRedisMap);

    }
}
