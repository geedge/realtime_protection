package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper {
    void newTask(@Param("task") Task task);

    void newTaskStaticRuleConcat(@Param("task_id") Long taskId,
                                 @Param("rule_ids") List<Integer> staticRuleIds);

    void newTaskDynamicRuleConcat(@Param("task_id") Long taskId,
                                  @Param("rule_ids") List<Integer> dynamicRuleIds);

    void newTaskUsingCommandInfo(@Param("info") TaskCommandInfo taskCommandInfo);

    List<Task> queryTasks(@Param("task_status") Integer taskStatus, @Param("task_type") Integer task_type,
                          @Param("task_name") String taskName, @Param("task_creator") String taskCreator,
                          @Param("page") Integer page, @Param("page_size") Integer pageSize);

    Task queryTask(@Param("task_id") Long taskId);

    void updateTask(@Param("task") Task task);

    void clearTaskConnectedStaticRule(@Param("task_id") Long taskId);

    void clearTaskConnectedDynamicRule(@Param("task_id") Long taskId);

    void changeTaskAuditStatus(@Param("task_id") Long taskId, @Param("audit_status") Integer auditStatus);

    Boolean deleteTask(@Param("task_id") Long taskId);

    Boolean changeTaskStatus(@Param("task_id") Long taskId, @Param("state") Integer stateNum);

    List<TaskCommandInfo> getStaticCommandInfos(@Param("task_id") Long taskId);

    List<DynamicTaskInfo> getDynamicTaskInfos(@Param("task_id") Long taskId);

    Integer queryTaskAuditStatus(@Param("task_id") Long taskId);

    Integer queryTaskStatus(@Param("task_id") Long taskId);

    List<Integer> queryDynamicRuleIdsFromTaskId(@Param("task_id") Long taskId);

    List<Integer> queryStaticRuleIdsFromTaskId(@Param("task_id") Long taskId);

    @Select("SELECT task_id FROM t_task WHERE task_end_time < NOW() AND task_status != #{task_status}")
    List<Long> queryTasksByStatus(@Param("task_status") Integer taskStatus);


    Integer queryTaskTotalNum(@Param("task_status") Integer taskStatus, @Param("task_type") Integer task_type,
                              @Param("task_name") String taskName, @Param("task_creator") String taskCreator);
}
