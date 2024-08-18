package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {


    void setIpTotalNum(@Param("ip_total_num")Long ipTotalNum, @Param("task_id")Long taskId) ;

    void newTask(@Param("task") Task task);

    void newTaskStaticRuleConcat(@Param("task_id") Long taskId,
                                 @Param("rule_ids") List<Integer> staticRuleIds,
                                 @Param("status") Integer usingStatusNum);

    void newTaskDynamicRuleConcat(@Param("task_id") Long taskId,
                                  @Param("rule_ids") List<Integer> dynamicRuleIds,
                                  @Param("status") Integer usingStatusNum);

    void newTaskUsingCommandInfo(@Param("info") TaskCommandInfo taskCommandInfo);

    List<Task> queryTasks(@Param("task_status") Integer taskStatus, @Param("task_type") Integer task_type,
                          @Param("task_name") String taskName, @Param("task_creator") String taskCreator,
                          @Param("audit_status") Integer auditStatus,
                          @Param("task_act")String taskAct, @Param("task_auditor")String taskAuditor,
                          @Param("task_source")String taskSource, @Param("rule_name")String ruleName,
                          @Param("event_type")String eventType,
                          @Param("create_date") String createDateStr,
                          @Param("start_date")String startDateStr,
                          @Param("protect_level")Integer protectLevel,
                          @Param("page") Integer page, @Param("page_size") Integer pageSize);

    Task queryTask(@Param("task_id") Long taskId);

    void updateTask(@Param("task") Task task);

    void clearTaskConnectedStaticRule(@Param("task_id") Long taskId, @Param("status")Integer num);

    void clearTaskConnectedDynamicRule(@Param("task_id") Long taskId, @Param("status")Integer num);

    void changeTaskAuditStatusWithAudior(@Param("task_id") Long taskId, @Param("audit_status") Integer auditStatus,
                               @Param("auditUserName")String auditUserName,
                               @Param("auditUserId")String auditUserId,
                               @Param("auditUserDepart")String auditUserDepart);

    void changeTaskAuditStatus(@Param("task_id") Long taskId, @Param("audit_status") Integer auditStatus);

    Boolean deleteTask(@Param("task_id") Long taskId);

    Boolean changeTaskStatus(@Param("task_id") Long taskId, @Param("state") Integer stateNum);

    List<TaskCommandInfo> getStaticCommandInfos(@Param("task_id") Long taskId);

    List<DynamicTaskInfo> getDynamicTaskInfos(@Param("task_id") Long taskId);

    Integer queryTaskAuditStatus(@Param("task_id") Long taskId);

    Integer queryTaskStatus(@Param("task_id") Long taskId);

    List<Integer> queryDynamicRuleIdsFromTaskId(@Param("task_id") Long taskId,
                                                @Param("audit_status_list") List<Integer> auditStatusList);

    List<Integer> queryStaticRuleIdsFromTaskId(@Param("task_id") Long taskId,
                                               @Param("audit_status_list") List<Integer> auditStatusList);

    @Select("SELECT task_id FROM t_task WHERE task_end_time < NOW() AND task_status != #{task_status}")
    List<Long> queryTasksByStatus(@Param("task_status") Integer taskStatus);


    Integer queryTaskTotalNum(@Param("task_status") Integer taskStatus, @Param("task_type") Integer task_type,
                              @Param("task_name") String taskName, @Param("task_creator") String taskCreator,
                              @Param("audit_status") Integer auditStatus,
                              @Param("task_act")String taskAct, @Param("task_auditor")String taskAuditor,
                              @Param("task_source")String taskSource, @Param("rule_name")String ruleName,
                              @Param("task_create_time") LocalDateTime taskCreateTime,
                              @Param("event_type")String eventType,
                              @Param("create_date")String createDate,
                              @Param("start_date")String startDate,
                              @Param("protect_level")Integer protectLevel);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    Integer queryAuditTaskTotalNum(Integer auditState);

    List<Integer> queryAuditStatusByIds(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idsWithAuditStatusMap);

    Boolean updateAuditInfo(@Param("ids")List<Long> ids, @Param("auditInfo")String auditInfo);

    String queryAuditInfo(Integer id);

    void updateAuditStatusWithAuditorByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch,
                                               @Param("auditUserName")String auditUserName,
                                               @Param("auditUserId")String auditUserId,
                                               @Param("auditUserDepart")String auditUserDepart);

    void newTaskProtectObjectConcat(@Param("taskId")Long taskId,
                                    @Param("protectObjectIds")List<Integer> protectObjectIds);

    void deleteTaskProtectObjectConcat(Long taskId);

    List<ProtectObject> queryProtectObjectsByTaskId(Long id);

    void insertTaskStatusLog(Long taskId);

    void updateTaskStatusLogExpireTime(Long taskId);

    void insertTaskStatusLogBatch(List<Integer> idWithAuditStatusBatch);

    void updateTaskStatusLogExpireTimeBatch(List<Integer> taskIds);

    List<Task> queryHistory(Long id, Integer page, Integer pageSize);

    @Select("SELECT task_id FROM t_task WHERE task_start_time <= NOW() " +
            "AND task_status = #{stateNum} AND task_audit_status = #{AuditNum}")
    List<Long> queryRunnableTasks(Integer stateNum, Integer AuditNum);

    @Update("UPDATE t_task SET task_start_time = NOW() WHERE task_id = #{taskId}")
    void updateTaskStartTime(Long taskId);

    String queryEventTypeDepartment(String eventType);

    String queryEventTypeAct(String eventType);

    List<Task> queryAuditInfoNotification(String userId , Integer auditStatus);

    void updateNotificationByTaskId(@Param("taskIds") List<Long> taskIdList);

    void saveFile(File saveFile);

    File selectFileById(Integer id);

    List<File> selectFilesByStaticRuleId(Long taskId);

    List<Task> queryTaskStausLog(Long id, Integer page, Integer pageSize);

    Integer queryTaskStausLogTotalNum(Long id);
}
