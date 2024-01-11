package com.realtime.protection.configuration.satoken.permission;

import com.realtime.protection.configuration.satoken.Nameable;

public interface Permission extends Nameable {
    default String getName() {
        return this.getClass().getSimpleName() + ":" + this.name();
    }
}
