package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {
    void newTask(@Param("task") Task task);

    void newTaskProtectObjectConcat(@Param("task_id") Integer taskId, @Param("proobj_id") Integer proobjId);

    void newTaskStaticRuleConcat(@Param("task_id") Integer taskId,
                                 @Param("rule_ids") List<Integer> staticRuleIds);

    void newTaskDynamicRuleConcat(@Param("task_id") Integer taskId,
                                  @Param("rule_ids") List<Integer> dynamicRuleIds);

    List<Task> queryTasks(@Param("task_status") Integer taskStatus, @Param("task_type") String task_type,
                          @Param("task_name") String taskName, @Param("task_creator") String taskCreator,
                          @Param("page") Integer page, @Param("page_size") Integer pageSize);

    Task queryTask(@Param("task_id") Integer taskId);

    List<Integer> queryTaskConcatProtectObjectIds(@Param("task_id") Integer taskId);

    List<Integer> queryTaskConcatStaticRuleIds(@Param("task_id") Integer taskId);

    List<Integer> queryTaskConcatDynamicRuleIds(@Param("task_id") Integer taskId);

    void updateTask(@Param("task") Task task);

    void clearTaskProtectObjectConcat(@Param("task_id") Integer taskId);

    void clearTaskConnectedStaticRule(@Param("task_id") Integer taskId);

    void clearTaskConnectedDynamicRule(@Param("task_id") Integer taskId);
}
