package com.realtime.protection.server.command;

import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommandMapper {
    Boolean createCommand(@Param("info") TaskCommandInfo taskCommandInfo);

    void createCommands(@Param("command_infos") List<TaskCommandInfo> taskCommandInfos);

    Boolean stopCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean removeCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean startCommandsByTaskId(@Param("task_id") Long taskId);

    List<TaskCommandInfo> queryCommandInfoByTaskId(@Param("task_id") Long taskId);
}
