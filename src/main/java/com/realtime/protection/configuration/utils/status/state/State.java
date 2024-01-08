package com.realtime.protection.configuration.utils.status.state;

public interface State {

    Boolean handle(State newState);
}
