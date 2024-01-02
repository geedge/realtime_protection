package com.realtime.protection.configuration.entity.user;

import lombok.Data;

@Data
public class User {
    private int userID;

    private String username;

    private String password;

    private String userDepart;
}
