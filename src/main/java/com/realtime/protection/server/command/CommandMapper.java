package com.realtime.protection.server.command;

import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommandMapper {
    void createCommand(@Param("info") TaskCommandInfo taskCommandInfo);

    void createCommands(@Param("command_infos") List<TaskCommandInfo> taskCommandInfos);

    Boolean stopCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean removeCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean startCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean setCommandJudged(@Param("command_id") String commandId,
                             @Param("is_judged") Boolean isJudged);

    List<TaskCommandInfo> queryCommandInfos(@Param("task_id") Long taskId,
                                            @Param("src_ip") String sourceIP,
                                            @Param("src_port") String sourcePort,
                                            @Param("dst_ip") String destinationIP,
                                            @Param("dst_port") String destinationPort,
                                            @Param("page") Integer page,
                                            @Param("page_num") Integer pageNum);

    TaskCommandInfo queryCommandInfoByUUID(@Param("uuid") String uuid);

    String queryCommandInfo(@Param("command_info") TaskCommandInfo commandInfo);
}
