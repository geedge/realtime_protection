package com.realtime.protection.server.user.login;

import cn.dev33.satoken.stp.StpUtil;
import com.realtime.protection.configuration.entity.user.User;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
// just for example, not for production environment
public class LoginService {

    private final LoginMapper loginMapper;

    public LoginService(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    public Integer login(User user) throws LoginException {
        String username = user.getUsername();
        String password = user.getPassword();

        Integer userId = loginMapper.login(username, password);
        if (userId == null) {
            throw new LoginException();
        }

        StpUtil.login(userId);
        return userId;
    }
}
