package com.realtime.protection.configuration.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.realtime.protection.configuration.satoken.permission.Permission;
import com.realtime.protection.configuration.satoken.role.Role;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
//                    SaRouter.match("/**")
//                            .notMatch("/user/doLogin")
//                            .check(r -> StpUtil.checkLogin());

//                    SaRouter.match("/whiteobj/new", r ->
//                            this.checkPermissions(WhiteList.NEW));
//                    SaRouter.match("/whiteobj/{id}/update", r ->
//                            this.checkPermissions(WhiteList.UPDATE));
        })).addPathPatterns("/**");
    }

    void checkPermissions(Permission... permissions) {
        for (Permission permission : permissions) {
            StpUtil.checkPermission(permission.getName());
        }
    }

    void checkRole(Role... roles) {
        for (Role role : roles) {
            StpUtil.checkRole(role.getName());
        }
    }
}

