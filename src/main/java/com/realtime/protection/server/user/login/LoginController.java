package com.realtime.protection.server.user.login;

import javax.security.auth.login.LoginException;

import com.realtime.protection.configuration.utils.EntityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.realtime.protection.configuration.entity.user.User;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Just for example, not in production environment
@CrossOrigin(origins = "*")
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
                               @Autowired HttpServletResponse response,
                               @RequestParam(value = "scopes", required = false) String scopes) {
        try {
            UserFull userFull = loginService.loginWithSSO(sessionData);
            if (userFull == null) {
                throw new LoginException("登录失败，无法获取用户信息");
            }
            // 设置 session
            HttpSession session = request.getSession();
            session.setAttribute("user", userFull);

            String sessionId = session.getId();

            Map<String, String> userRedisMap = new HashMap<>();
            userRedisMap.put("sessionData", sessionData);
            userRedisMap.put("userId", userFull.uid);
            userRedisMap.put("userName", userFull.name);
            userRedisMap.put("userRole", userFull.getRoleKey());
            userRedisMap.put("UserDepartmentName", userFull.getOrgName());
            userRedisMap.put("UserDepartmentCode", userFull.getOrgCode());


            if (!loginService.storeUserFullToRedis(userRedisMap)){
                throw new LoginException("登录失败，无法存储用户信息到Redis");
            }

            // 设置JSESSIONID Cookie
            Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
            sessionCookie.setPath("/api"); // 确保路径正确
            sessionCookie.setHttpOnly(true); // 防止客户端脚本访问
            response.addCookie(sessionCookie);

            // 返回结果
            return ResponseResult.ok().setMessage("success")
                .setData("userId", userFull.uid)
                .setData("userName", userFull.name)
                .setData("userRole", userFull.getRoleKey())
                .setData("UserDepartment", userFull.getOrgName())
                .setData("userdepartment", userFull.getOrgCode())
                ;
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
