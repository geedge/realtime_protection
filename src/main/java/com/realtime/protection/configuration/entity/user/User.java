package com.realtime.protection.configuration.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userId;

    private String deptId;

    private String username;

    private String password;

    private String userDepart;
}
