package com.realtime.protection.server.task.status;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.status.State;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@EnableScheduling
@Slf4j
public class StateChangeService {
    private final CommandService commandService;
    private final TaskService taskService;

    public StateChangeService(CommandService commandService, TaskService taskService) {
        this.commandService = commandService;
        this.taskService = taskService;
    }

    @DSTransactional
    public Boolean changeState(Integer stateNum, Long taskId, Boolean inner) throws DorisStartException {
        Integer originalStateNum = taskService.queryTaskStatus(taskId);
        if (originalStateNum == null) {
            throw new IllegalArgumentException("无法找到" + taskId + "的任务状态，也许任务ID不存在?");
        }

        State originalState = StateEnum.getStateByNum(originalStateNum);

        State newState = StateEnum.getStateByNum(stateNum);

        if (!inner && !checkState(originalState, newState)) {
            throw new IllegalArgumentException(
                    String.format("任务状态转换失败，原状态：%s，欲切换状态：%s",
                            originalState.getClass().getSimpleName(),
                            newState.getClass().getSimpleName()));
        }

        if (Objects.equals(originalState, newState)) {
            return true;
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

    private Boolean checkState(State originalState, State newState) {
        if (originalState == null || newState == null) {
            return false;
        }

        // FAILED、FINISHED状态以及GENERATING都只能在程序内部修改，外部接口不能修改
        if (Objects.equals(newState, StateEnum.FAILED.getState())
                || Objects.equals(newState, StateEnum.FINISHED.getState())
                || Objects.equals(newState, StateEnum.GENERATING.getState())) {
            return false;
        }

        // 在任务状态转换为GENERATING之后，我们需要在外部接口屏蔽掉所有状态
        // 我们需要保证只有任务创建函数才能将GENERATING状态转换为RUNNING状态
        return !Objects.equals(originalState, StateEnum.GENERATING.getState());
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    @Transactional
    @Async
    protected void finishTasks() {
        List<Long> finishedTaskIds = taskService.getFinishedTasks();
        log.debug("成功扫描出所有需要变为结束状态的任务：" + finishedTaskIds);

        for (Long taskId : finishedTaskIds) {
            try {
                changeState(StateEnum.FINISHED.getStateNum(), taskId, true);
            } catch (Exception e) {
                log.warn(String.format("任务%d从%s状态变为FINISHED状态遭遇异常：%s",
                        taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
            }

        }
    }
}
