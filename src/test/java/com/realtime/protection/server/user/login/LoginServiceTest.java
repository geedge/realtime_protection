package com.realtime.protection.server.user.login;

import com.realtime.protection.configuration.entity.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.security.auth.login.LoginException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginServiceTest {

    private final LoginService loginService;

    @Autowired
    LoginServiceTest(LoginService loginService) {
        this.loginService = loginService;
    }

    @Test
    void testLoginFail() {
        User user = new User();
        user.setPassword("12345");
        user.setUsername("endera");

        assertThrows(LoginException.class, () -> loginService.login(user));

        user.setUsername("");
        user.setPassword("");
        assertThrows(LoginException.class, () -> loginService.login(user));
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("endera");
        user.setPassword("123456");

        assertDoesNotThrow(() -> assertEquals(1, loginService.login(user)));
    }
}