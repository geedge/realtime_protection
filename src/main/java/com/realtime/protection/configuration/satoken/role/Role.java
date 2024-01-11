package com.realtime.protection.configuration.satoken.role;

import com.realtime.protection.configuration.satoken.Nameable;

public interface Role extends Nameable {
    default String getName() {
        return this.getClass().getSimpleName() + ":" + this.name();
    }

}
