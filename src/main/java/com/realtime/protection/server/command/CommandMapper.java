package com.realtime.protection.server.command;

import com.realtime.protection.configuration.entity.task.Command;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommandMapper {
    Boolean createCommand(@Param("command") Command command);

    void createCommands(@Param("commands") List<Command> commands);

    Boolean stopCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean removeCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean startCommandsByTaskId(@Param("task_id") Long taskId);
}
