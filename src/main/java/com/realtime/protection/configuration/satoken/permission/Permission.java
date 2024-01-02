package com.realtime.protection.configuration.satoken.permission;

public interface Permission extends Nameable {
    default String getName() {
        return this.getClass().getSimpleName() + ":" + this.name();
    }
}
