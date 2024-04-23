package com.realtime.protection.server.task;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
@DS("mysql")
public class TaskService {
    private final TaskMapper taskMapper;
    private final SqlSessionWrapper sqlSessionWrapper;

    public TaskService(TaskMapper taskMapper,SqlSessionWrapper sqlSessionWrapper) {
        this.taskMapper = taskMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }

    @Transactional
    public Long newTask(Task task) {
        task.setTaskCreateUserId(1);
        task.setTaskCreateUsername("xxx");
        task.setTaskCreateDepart("xxx");

        // todo: 添加新建任务时，将动态/静态规则从“已审核”修改为“使用中”

        taskMapper.newTask(task);

        if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty())
            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());

        if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty())
            taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());

        return task.getTaskId();
    }

    @Transactional
    public List<Task> queryTasks(Integer taskStatus,
                                 Integer taskType, String taskName, String taskCreator,
                                 Integer auditStatus,
                                 Integer page, Integer pageSize) {
        List<Task> tasks = taskMapper.queryTasks(taskStatus, taskType, taskName, taskCreator, auditStatus, page, pageSize);
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            task.setStaticRuleIds(taskMapper.queryStaticRuleIdsFromTaskId(task.getTaskId()));
            task.setDynamicRuleIds(taskMapper.queryDynamicRuleIdsFromTaskId(task.getTaskId()));
        }

        return tasks;
    }

    @Transactional
    public Task queryTask(Long id) {
        Task task = taskMapper.queryTask(id);
        if (task == null) {
            return null;
        }

        task.setStaticRuleIds(taskMapper.queryStaticRuleIdsFromTaskId(task.getTaskId()));
        task.setDynamicRuleIds(taskMapper.queryDynamicRuleIdsFromTaskId(task.getTaskId()));

        return task;
    }

    @Transactional
    public Boolean updateTask(Task task) {
        if (!Objects.equals(taskMapper.queryTaskAuditStatus(task.getTaskId()), AuditStatusEnum.AUDITED.getNum())) {
            return false;
        }

        task.setTaskAuditStatus(AuditStatusEnum.PENDING.getNum());

        taskMapper.updateTask(task);

        taskMapper.clearTaskConnectedStaticRule(task.getTaskId());
        taskMapper.clearTaskConnectedDynamicRule(task.getTaskId());

        if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty())
            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());

        if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty())
            taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());

        return true;
    }

    @Transactional
    public Boolean changeTaskAuditStatus(Long taskId, Integer taskAuditStatus) {
        Integer originalAuditStatus = taskMapper.queryTaskAuditStatus(taskId);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("无法找到任务ID为" + taskId + "的任务，也许任务不存在？");
        }

        if (AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(taskAuditStatus))
            taskMapper.changeTaskAuditStatus(taskId, taskAuditStatus);
        else return false;

        return true;
    }

    public Boolean deleteTask(Long taskId) {
        return taskMapper.deleteTask(taskId);
    }


    public Boolean changeTaskStatus(Long taskId, Integer stateNum) {
        return taskMapper.changeTaskStatus(taskId, stateNum);
    }

    public List<TaskCommandInfo> getStaticCommandInfos(Long taskId) {
        List<TaskCommandInfo> staticCommandInfos = taskMapper.getStaticCommandInfos(taskId);

        staticCommandInfos.forEach(taskCommandInfo -> {
            taskCommandInfo.setProtocolNum();
            taskCommandInfo.setMask();
        });

        return staticCommandInfos;
    }

    public List<DynamicTaskInfo> getDynamicTaskInfos(Long taskId) {
        return taskMapper.getDynamicTaskInfos(taskId);
    }

    public Integer queryTaskAuditStatus(Long taskId) {
        return taskMapper.queryTaskAuditStatus(taskId);
    }

    public Integer queryTaskStatus(Long taskId) {
        return taskMapper.queryTaskStatus(taskId);
    }

    public Long newTaskUsingCommandInfo(TaskCommandInfo taskCommandInfo) {
        taskMapper.newTaskUsingCommandInfo(taskCommandInfo);
        return taskCommandInfo.getTaskId();
    }

    public List<Long> getFinishedTasks() {
        return taskMapper.queryTasksByStatus(StateEnum.FINISHED.getStateNum());
    }

    public Integer queryTaskTotalNum(Integer taskStatus, Integer taskType, String taskName, String taskCreator) {
        return taskMapper.queryTaskTotalNum(taskStatus, taskType, taskName, taskCreator);
    }

    public Object updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        Function<TaskMapper, Function<Map<Integer, Integer>, Boolean>> updateTaskAuditStatusFunction =
                mapper -> map -> {
                    if (map == null || map.isEmpty()) {
                        return false;
                    }

                    Map<Integer, Integer> idWithAuditStatusBatch = new HashMap<>();
                    for (Map.Entry<Integer, Integer> item : map.entrySet()) {
                        idWithAuditStatusBatch.put(item.getKey(), item.getValue());
                        if (idWithAuditStatusBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(TaskMapper.class, updateTaskAuditStatusFunction, idsWithAuditStatusMap);


    }
}
