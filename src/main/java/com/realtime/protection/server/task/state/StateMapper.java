package com.realtime.protection.server.task.state;

import com.realtime.protection.configuration.entity.task.Command;
import org.apache.ibatis.annotations.Param;

public interface StateMapper {
    Boolean sendCommand(@Param("command") Command command);
}
