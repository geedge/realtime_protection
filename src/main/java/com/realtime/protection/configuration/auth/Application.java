package com.realtime.protection.configuration.auth;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Yixiang Zhao (@seriouszyx)
 **/
@SpringBootApplication
@EnableCaching
public class Application implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration ir = registry.addInterceptor(new LoginInterceptor());
//        ir.addPathPatterns("/**");
//        ir.excludePathPatterns("/js/**", "/html/**", "/image/**", "/css/**", "/api/**");
    }
}

