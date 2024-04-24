package com.realtime.protection.server.user.login;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @PostMapping("/auth")
    public ResponseResult auth(@RequestParam("sessionData") String sessionData,
                               @Autowired HttpServletRequest request,
                               @RequestParam(value = "scopes", required = false) String scopes) {
        try {
            UserFull userFull = loginService.loginWithSSO(sessionData);
            if (userFull == null) {
                throw new LoginException("登录失败，无法获取用户信息");
            }
            // 设置 session
            HttpSession session = request.getSession();
            session.setAttribute("user", userFull);
            // 返回结果
            return ResponseResult.ok().setMessage("success")
                .setData("userId", userFull.uid)
                .setData("userName", userFull.name)
                .setData("userRole", userFull.getRoleKey());
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }
    }

    @GetMapping("/auth_redirect")
    public String authRedirect(@RequestParam(value = "SESSION_DATA", required = false) String sessionData) {
        System.out.println("SESSION_DATA: " + sessionData);
        return "";
    }
}
