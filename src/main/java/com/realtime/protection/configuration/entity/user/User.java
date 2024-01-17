package com.realtime.protection.configuration.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long userId;

    private Long deptId;

    private String username;

    private String password;

    private String userDepart;
}
