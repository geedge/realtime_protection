package com.realtime.protection.configuration.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.realtime.protection.configuration.satoken.permission.Permission;
import com.realtime.protection.configuration.satoken.permission.SystemConfiguration;
import com.realtime.protection.configuration.satoken.permission.WhiteList;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    SaRouter.match("/whiteobj/new", r ->
                            this.checkPermissions(SystemConfiguration.NEW, WhiteList.NEW));
                    SaRouter.match("/whiteobj/update", r ->
                            this.checkPermissions(SystemConfiguration.UPDATE, WhiteList.UPDATE));
                }))
                .addPathPatterns("/**")
                .excludePathPatterns("/user/doLogin");
    }

    void checkPermissions(Permission... permissions) {
        for (Permission permission : permissions) {
            StpUtil.checkPermission(permission.getName());
        }
    }

}

