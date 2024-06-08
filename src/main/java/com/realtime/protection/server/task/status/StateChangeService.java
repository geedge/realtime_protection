package com.realtime.protection.server.task.status;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.task.status.states.State;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 任务状态管理服务类
 */
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

    /**
     * 切换任务状态
     * @param stateNum 欲切换的任务状态编号（编号对应的状态请查看configuration/utils/enums/StateEnum类）
     * @param taskId 任务ID
     * @param inner 此接口是否为内部调用，如果为内部调用，则取消所有的状态检查
     * @return 状态切换是否成功
     * @throws DorisStartException Doris数据库异常
     */
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

        // 切换到states文件夹中查看每一个状态的handle函数
        if (!originalState.handle(newState, commandService, taskService, taskId)) {
            return false;
        }

        log.info(String.format("成功使得task(%d)从%s切换为%s",
                taskId,
                originalState.getClass().getSimpleName(),
                newState.getClass().getSimpleName()));


        // 这里一定是handle成功的状态，我们再进行task status的修改，如果handle失败，要么返回false，要么抛出异常，不会进入此处
        Boolean isChanged = taskService.changeTaskStatus(taskId, stateNum);
        //记录状态日志
        if (isChanged) {
            taskService.insertTaskStatusLog(taskId);
        }

        return isChanged;
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

    /**
     * 将任务切换为结束状态
     */
    @Scheduled(cron = "0/10 * * * * ?")
    @Async
    protected void finishTasks() {
        List<Long> finishedTaskIds = taskService.getFinishedTasks();
        log.debug("成功扫描出所有需要变为结束状态的任务：{}", finishedTaskIds);

        for (Long taskId : finishedTaskIds) {
            try {
                changeState(StateEnum.FINISHED.getStateNum(), taskId, true);
            } catch (Exception e) {
                log.warn(String.format("任务%d从%s状态变为FINISHED状态遭遇异常：%s",
                        taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
            }

        }
    }

    /**
     * 将任务切换为开始状态
     */
    @Scheduled(cron = "0/10 * * * * ?")
    @Async
    protected void startTasks() {
        List<Long> runnableTaskIds = taskService.getRunnableTasks();
        log.debug("成功扫描出所有需要变为开始状态的任务：{}", runnableTaskIds);

        for (Long taskId : runnableTaskIds) {
            try {
                changeState(StateEnum.RUNNING.getStateNum(), taskId, true);
            } catch (Exception e) {
                log.warn(String.format("任务%d从%s状态变为运行中RUNNING状态遭遇异常：%s",
                        taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
            }

        }
    }

}
