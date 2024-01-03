package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMapper {
    void newTask(@Param("task") Task task);

    void newTaskProobjConcat(@Param("task_id") Integer taskId, @Param("proobj_ids") Integer[] proobjIds);

    Integer newTaskStaticRuleConcat(@Param("task_id") Integer taskId,
                                    @Param("rule_ids") Integer[] staticRuleIds);

    Integer newTaskDynamicRuleConcat(@Param("task_id") Integer taskId,
                                     @Param("rule_ids") Integer[] dynamicRuleIds);
}
