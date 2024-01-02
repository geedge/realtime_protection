package com.realtime.protection.server.user.login;

import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

// Just for example, not in production environment
@RestController
@RequestMapping("/user")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/doLogin")
    public ResponseResult doLogin(@RequestBody User user) {
        Integer userId;

        try {
            userId = loginService.login(user);
        } catch (LoginException e) {
            return ResponseResult.error().setMessage("User not valid")
                    .setData("userId", null)
                    .setData("success", false);
        }

        return ResponseResult.ok().setMessage("success")
                .setData("userId", userId)
                .setData("success", true);
    }
}
