package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.utils.status.AuditStatusValidator;
import com.realtime.protection.server.task.state.PauseState;
import com.realtime.protection.server.task.state.RunningState;
import com.realtime.protection.server.task.state.State;
import com.realtime.protection.server.task.state.StopState;
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
    public Integer newTask(Task task) {
        taskMapper.newTask(task);

        taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());
        taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());

        return task.getTaskId();
    }

    public List<Task> queryTasks(Integer taskStatus,
                                 String taskType, String taskName, String taskCreator,
                                 Integer page, Integer pageSize) {
        return taskMapper.queryTasks(taskStatus, taskType, taskName, taskCreator, page, pageSize);
    }

    public Task queryTask(Integer id) {
        return taskMapper.queryTask(id);
    }

    @Transactional
    public Boolean updateTask(Task task) {
        taskMapper.updateTask(task);

        taskMapper.clearTaskConnectedStaticRule(task.getTaskId());
        taskMapper.clearTaskConnectedDynamicRule(task.getTaskId());

        taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());
        taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());

        return true;
    }

    @Transactional
    public Boolean changeTaskAuditStatus(Integer taskId, Integer taskAuditStatus) {
        if (AuditStatusValidator.setOriginal(taskMapper.queryTask(taskId).getTaskAuditStatus()).checkValidate(taskAuditStatus))
            taskMapper.changeTaskAuditStatus(taskId, taskAuditStatus);
        else return false;

        return true;
    }

    public Boolean deleteTask(Integer taskId) {
        return taskMapper.deleteTask(taskId);
    }

    @Transactional
    public Boolean changeTaskStatus(Integer taskId, Integer stateNum) {
        State originalState = switch (taskMapper.queryTask(taskId).getTaskStatus()) {
            // 运行中
            case 1 -> new RunningState();
            // 暂停中
            case 2 -> new PauseState();
            // 停止中
            case 3 -> new StopState();
            default -> throw new IllegalArgumentException();
        };

        State newState = switch (stateNum) {
            // 运行中
            case 1 -> new RunningState();
            // 暂停中
            case 2 -> new PauseState();
            // 停止中
            case 3 -> new StopState();
            default -> throw new IllegalArgumentException();
        };

        return StatusChanger.setOriginal(originalState).changeState(newState);
    }
}
