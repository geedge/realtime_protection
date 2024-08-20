package com.realtime.protection.server.command;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommandMapper {
    void createCommand(@Param("info") TaskCommandInfo taskCommandInfo);

    void createCommands(@Param("command_infos") List<TaskCommandInfo> taskCommandInfos);

    void createCommandsTest(@Param("command_infos") List<TaskCommandInfo> taskCommandInfos,
                            @Param("session_num") Integer sessionNum,
                            @Param("byte_num") Integer byteNum);


    Boolean stopCommandsByTaskId(@Param("task_id") Long taskId);

    @DS("doris")
    Boolean removeCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean startCommandsByTaskId(@Param("task_id") Long taskId);

    Boolean setCommandJudged(@Param("command_id") String commandId,
                             @Param("is_judged") Integer isJudged);

    List<TaskCommandInfo> queryCommandInfos(@Param("task_id") Long taskId,
                                            @Param("src_ip") String sourceIP,
                                            @Param("src_port") String sourcePort,
                                            @Param("dst_ip") String destinationIP,
                                            @Param("dst_port") String destinationPort,
                                            @Param("page") Integer page,
                                            @Param("page_num") Integer pageNum);

    TaskCommandInfo queryCommandInfoByUUID(@Param("uuid") String uuid);

    String queryCommandInfo(@Param("command_info") TaskCommandInfo commandInfo);

    Integer queryCommandTotalNum(@Param("task_id") Long taskId,
                                 @Param("src_ip") String sourceIP,
                                 @Param("src_port") String sourcePort,
                                 @Param("dst_ip") String destinationIP,
                                 @Param("dst_port") String destinationPort);

    void createCommandInWhiteListHit(@Param("info") TaskCommandInfo commandInfo);


    void updateCommandHistoryExpireTime(@Param("command_id") String commandUUID);

    void insertCommandHistory(@Param("command_id") String commandUUID, @Param("log_id") String logUUID);

    void updateCommandHistoryExpireTimeBatch(@Param("commandIds")List<String> commandIds);

    void insertCommandHistoryBatch(@Param("commandIds")List<String> commandIds, @Param("command_status")Integer commandStatus);

    @DS("mysql")
    List<WhiteListObject> whiteListCommandCheck(@Param("command") FiveTupleWithMask fiveTupleWithMask);
    @DS("mysql")
    void createCommandWhiteListConnect(@Param("command_id") String uuid, @Param("whiteLists") List<WhiteListObject> whiteListsHit);

    void updateCommandIsJudgedIfIgnoreThisTime(@Param("command_id") String commandUUID);

    Integer queryCommandIsJudged(String uuid);

    void insertCommandDistribute(@Param("info") TaskCommandInfo commandInfo);

    void insertCommandRCPQuery(@Param("info") TaskCommandInfo commandInfo);

    void insertCommandTraffic(@Param("info") TaskCommandInfo commandInfo);

    void insertCommandDistributeBatch(@Param("command_infos")List<TaskCommandInfo> taskCommandInfoBatch);

    void insertCommandRCPQueryBatch(@Param("command_infos")List<TaskCommandInfo> taskCommandInfoBatch);

    void insertCommandTrafficBatch(@Param("command_infos")List<TaskCommandInfo> taskCommandInfoBatch);

    Boolean setCommandValid(String commandId, Integer isValid);

    List<TaskCommandInfo> queryAllDistributingCommandInfo();

    List<Integer> queryRunningCommandsDistributeStatusByTaskId(Long taskId);

    List<TaskCommandInfo> queryRunningCommandsRcpHitCount();

    Integer queryCommandLogRcpHitCountByCommandId(@Param("command_id")String commandId);

    void insertCommandHistoryWithStatus(@Param("command_id")String commandUUID,
                                        @Param("log_id")String logId,
                                        @Param("command_status")Integer commandStatus);

    List<String> queryCommandInfosByTaskId(@Param("task_id")Long taskId);

    List<TaskCommandInfo> queryRunningCommandsTotalPacketNum();

    Integer queryCommandLogTotalPacketNumByCommandId(@Param("command_id")String uuid);
}
