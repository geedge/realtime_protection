package com.realtime.protection.server.task.status;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.status.State;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class StateChangeService {
    private final CommandService commandService;
    private final TaskService taskService;

    public StateChangeService(CommandService commandService, TaskService taskService) {
        this.commandService = commandService;
        this.taskService = taskService;
    }

    @DSTransactional
    public Boolean changeState(Integer stateNum, Long taskId) throws DorisStartException {
        if (Objects.equals(stateNum, StateEnum.GENERATING.getStateNum()) ||
                Objects.equals(stateNum, StateEnum.FAILED.getStateNum())) {
            throw new IllegalArgumentException("非法任务状态：" + StateEnum.getStateByNum(stateNum));
        }

        Integer originalStateNum = taskService.queryTaskStatus(taskId);
        if (originalStateNum == null) {
            throw new IllegalArgumentException("无法找到" + taskId + "的任务状态，也许任务ID不存在?");
        }

        State originalState = StateEnum.getStateByNum(originalStateNum);

        State newState = StateEnum.getStateByNum(stateNum);

        if (newState == null) {
            return false;
        }

        if (!originalState.handle(newState, commandService, taskService, taskId)) {
            return false;
        }

        log.debug(String.format("成功使得task(%d)从%s切换为%s",
                taskId,
                originalState.getClass().getSimpleName(),
                newState.getClass().getSimpleName()));

        // 这里一定是handle成功的状态，我们再进行task status的修改，如果handle失败，要么返回false，要么抛出异常，不会进入此处
        return taskService.changeTaskStatus(taskId, stateNum);
    }
}
