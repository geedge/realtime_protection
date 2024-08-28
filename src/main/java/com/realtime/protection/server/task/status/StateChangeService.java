package com.realtime.protection.server.task.status;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.CommandStatusEnum;
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

    /**
     * 更新任务状态，要么加字段（运行中加两个）、“要么改状态模式（字典表也要改）”
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Async
    protected void updateTasksRunningStatus() {
        //查询所有正在运行中的任务
        List<Long> runningTaskIds = taskService.getRunningTasks();
        log.info("更新任务运行中状态：成功扫描出所有正在运行中的任务：{}", runningTaskIds);

        for (Long taskId : runningTaskIds) {
            //查询任务下所有的正在下发的指令（isjudge = 1 ，isvalid=1， is_deleted=0）的本次是否下发成功字段（t_command加个字段）
            List<Integer> commandStatusList = commandService.queryRunningCommandsDistributeStatusByTaskId(taskId);
            if (commandStatusList == null){
                continue;
            }
            //判断是否全为成功
            if (commandStatusList.stream().allMatch(status -> status == null || status == 0)) {
                //全为成功
                try {
                    changeState(StateEnum.RUNNING_SUCCESS.getStateNum(), taskId, true);
                    continue;
                } catch (Exception e) {
                    log.warn(String.format("任务%d从%s状态变为运行中RUNNING_SUCCESS状态遭遇异常：%s",
                            taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
                }
            }
            if (commandStatusList.stream().anyMatch(status -> status == null || status == 0)) {
                //部分成功
                try {
                    changeState(StateEnum.RUNNING_PARTIAL_SUCCESS.getStateNum(), taskId, true);
                    continue;
                } catch (Exception e) {
                    log.warn(String.format("任务%d从%s状态变为运行中RUNNING_PARTIAL_SUCCESS状态遭遇异常：%s",
                            taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
                }
            }
            //全失败
            try {
                changeState(StateEnum.RUNNING_FAILED.getStateNum(), taskId, true);
            } catch (Exception e) {
                log.warn(String.format("任务%d从%s状态变为运行中RUNNING_FAILED状态遭遇异常：%s",
                        taskId, taskService.queryTaskStatus(taskId), e.getMessage()));
            }

        }
    }


    /**
     * 更新每次指令下周内rcp\flow命中状态
     */
    @Scheduled(cron = "5/20 * * * * ?")
    @Async
    protected void updateCommandRCPHitStatus() {
        //获取所有正在下发的指令
        List<TaskCommandInfo> runningCommandList = commandService.queryRunningCommands();
        log.info("rcp命中状态更新：正在下发的指令数量：{}", runningCommandList.size());


        //遍历所有指令
        for (TaskCommandInfo taskCommandInfo : runningCommandList) {
            //查询状态表，查看本次下发周期内是否已经记录了命中状态。查询最近的两条日志记录
            List<TaskCommandInfo> commandLogs = commandService.queryCommandLogLastTwoRecord(taskCommandInfo.getUUID());
            if (commandLogs.size()<2){
                continue;
            }
            TaskCommandInfo commandLogLatest = commandLogs.get(0);
            TaskCommandInfo commandLogLatest2 = commandLogs.get(1);
            log.info("指令历史状态：{}", commandLogs);


            //如果日志状态为SEND、SUCCESS 或者 SEND、FAIL，则要根据当前t_command表的最新记录，和上次下发状态的命中计数相比判断是否有变化
            if (commandLogLatest2.getCommandStatus()==CommandStatusEnum.SEND.getCommandStatusNum()
                    && (commandLogLatest.getCommandStatus()==CommandStatusEnum.SUCCESS.getCommandStatusNum()
                    || commandLogLatest.getCommandStatus()==CommandStatusEnum.FAIL.getCommandStatusNum()))
            {
                log.info("rcp命中查询：{}，{}",taskCommandInfo.getRcpHitCount(), commandLogLatest.getRcpHitCount());
                if (taskCommandInfo.getRcpHitCount()!=null && commandLogLatest.getRcpHitCount()==null) {
                    //更新log表，rcp命中
                    commandService.insertCommandHistoryWithTimeWithTraffic(taskCommandInfo.getUUID(),
                            CommandStatusEnum.RCPHIT.getCommandStatusNum(), taskCommandInfo.getLastRCPQueryTime(),
                            commandLogLatest.getTotalPacketNum());
                }
                if (taskCommandInfo.getRcpHitCount()!=null && commandLogLatest.getRcpHitCount()!=null) {
                    if (taskCommandInfo.getRcpHitCount() > commandLogLatest.getRcpHitCount()) {
                        //更新log表，rcp命中
                        commandService.insertCommandHistoryWithTimeWithTraffic(taskCommandInfo.getUUID(),
                                CommandStatusEnum.RCPHIT.getCommandStatusNum(), taskCommandInfo.getLastRCPQueryTime(),
                                commandLogLatest.getTotalPacketNum());
                    }
                }


                log.info("流量命中查询：{}，{}",taskCommandInfo.getTotalPacketNum(), commandLogLatest.getTotalPacketNum());
                if (taskCommandInfo.getTotalPacketNum()!=null && commandLogLatest.getTotalPacketNum()==null) {
                    //更新log表，流量命中
                    commandService.insertCommandHistoryWithTime(taskCommandInfo.getUUID(),
                            CommandStatusEnum.FLOWHIT.getCommandStatusNum(),taskCommandInfo.getLastTrafficQueryTime());
                }
                if (taskCommandInfo.getTotalPacketNum()!=null && commandLogLatest.getTotalPacketNum()!=null) {
                    if (taskCommandInfo.getTotalPacketNum() > commandLogLatest.getTotalPacketNum()) {
                        //更新log表，流量命中
                        commandService.insertCommandHistoryWithTime(taskCommandInfo.getUUID(),
                                CommandStatusEnum.FLOWHIT.getCommandStatusNum(),taskCommandInfo.getLastTrafficQueryTime());
                    }
                }

                continue;
            }


            //如果日志状态为SUCCESS/FAIL、RCPHIT/UNRCPHIT，则判断是否更新流量命中状态
            if ((commandLogLatest2.getCommandStatus()==CommandStatusEnum.SUCCESS.getCommandStatusNum()
                    || commandLogLatest2.getCommandStatus()==CommandStatusEnum.FAIL.getCommandStatusNum())
                    && (commandLogLatest.getCommandStatus()==CommandStatusEnum.RCPHIT.getCommandStatusNum()
                    || commandLogLatest.getCommandStatus()==CommandStatusEnum.UNRCPHIT.getCommandStatusNum()))
            {

                log.info("流量命中查询：{}，{}",taskCommandInfo.getTotalPacketNum(), commandLogLatest.getTotalPacketNum());
                if (taskCommandInfo.getTotalPacketNum()!=null && commandLogLatest.getTotalPacketNum()==null) {
                    //更新log表，流量命中
                    commandService.insertCommandHistory(taskCommandInfo.getUUID(),
                            CommandStatusEnum.FLOWHIT.getCommandStatusNum());
                }
                if (taskCommandInfo.getTotalPacketNum()!=null && commandLogLatest.getTotalPacketNum()!=null) {
                    if (taskCommandInfo.getTotalPacketNum() > commandLogLatest.getTotalPacketNum()) {
                        //更新log表，流量命中
                        commandService.insertCommandHistory(taskCommandInfo.getUUID(),
                                CommandStatusEnum.FLOWHIT.getCommandStatusNum());
                    }
                }
                continue;
            }
            //如果日志状态为SUCCESS/FAIL、FLOWHIT/UNFLOWHIT，则判断是否更新rcp命中状态
            if ((commandLogLatest2.getCommandStatus()==CommandStatusEnum.SUCCESS.getCommandStatusNum()
                    || commandLogLatest2.getCommandStatus()==CommandStatusEnum.FAIL.getCommandStatusNum())
                    && (commandLogLatest.getCommandStatus()==CommandStatusEnum.FLOWHIT.getCommandStatusNum()
                    || commandLogLatest.getCommandStatus()==CommandStatusEnum.UNFLOWHIT.getCommandStatusNum()))
            {
                log.info("rcp命中查询：{}，{}",taskCommandInfo.getRcpHitCount(), commandLogLatest.getRcpHitCount());
                if (taskCommandInfo.getRcpHitCount()!=null && commandLogLatest.getRcpHitCount()==null) {
                    //更新log表，rcp命中
                    commandService.insertCommandHistoryWithTime(taskCommandInfo.getUUID(),
                            CommandStatusEnum.RCPHIT.getCommandStatusNum(), taskCommandInfo.getLastRCPQueryTime());
                }
                if (taskCommandInfo.getRcpHitCount()!=null && commandLogLatest.getRcpHitCount()!=null) {
                    if (taskCommandInfo.getRcpHitCount() > commandLogLatest.getRcpHitCount()) {
                        //更新log表，rcp命中
                        commandService.insertCommandHistoryWithTime(taskCommandInfo.getUUID(),
                                CommandStatusEnum.RCPHIT.getCommandStatusNum(), taskCommandInfo.getLastRCPQueryTime());
                    }
                }
                continue;
            }
            //如果日志状态为RCPHIT/FLOWHIT、FLOWHIT/UNFLOWHIT，则不需要查询了
        }

    }


}
