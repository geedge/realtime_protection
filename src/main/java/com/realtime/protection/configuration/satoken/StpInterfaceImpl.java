package com.realtime.protection.configuration.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.realtime.protection.configuration.satoken.permission.SystemConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        ArrayList<String> list = new ArrayList<>();

        if (loginId.equals("endera")) {
            list.add(SystemConfiguration.NEW.getName());
        }

        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }

}

