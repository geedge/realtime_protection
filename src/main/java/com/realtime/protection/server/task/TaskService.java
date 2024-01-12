package com.realtime.protection.server.task;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.status.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    private final TaskMapper taskMapper;

    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Transactional
    public Long newTask(Task task) {
        taskMapper.newTask(task);

        if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty())
            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());

        if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty())
            taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());

        return task.getTaskId();
    }

    public List<Task> queryTasks(Integer taskStatus,
                                 String taskType, String taskName, String taskCreator,
                                 Integer page, Integer pageSize) {
        return taskMapper.queryTasks(taskStatus, taskType, taskName, taskCreator, page, pageSize);
    }

    public Task queryTask(Long id) {
        return taskMapper.queryTask(id);
    }

    @Transactional
    public Boolean updateTask(Task task) {
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
            throw new IllegalArgumentException("cannot find audit status of task " + taskId + ", maybe task doesn't exist?");
        }

        if (AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(taskAuditStatus))
            taskMapper.changeTaskAuditStatus(taskId, taskAuditStatus);
        else return false;

        return true;
    }

    public Boolean deleteTask(Long taskId) {
        return taskMapper.deleteTask(taskId);
    }

    @DS("mysql")
    public Boolean changeTaskStatus(Long taskId, Integer stateNum) {
        return taskMapper.changeTaskStatus(taskId, stateNum);
    }

    public List<TaskCommandInfo> getStaticCommandInfos(Long taskId) {
        return taskMapper.getStaticCommands(taskId);
    }

    public Integer queryTaskAuditStatus(Long taskId) {
        return taskMapper.queryTaskAuditStatus(taskId);
    }

    public Integer queryTaskStatus(Long taskId) {
        return taskMapper.queryTaskStatus(taskId);
    }
}
