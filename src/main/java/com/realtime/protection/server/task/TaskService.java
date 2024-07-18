package com.realtime.protection.server.task;

import com.alibaba.excel.util.MapUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.File;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import com.realtime.protection.server.command.CommandMapper;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleMapper;
import com.realtime.protection.server.rule.staticrule.StaticRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@DS("mysql")
public class TaskService {
    private final TaskMapper taskMapper;
    private final StaticRuleMapper staticRuleMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final int BATCH_SIZE = 100;
    private final DynamicRuleMapper dynamicRuleMapper;
    private final Counter counter;
    private final CommandMapper commandMapper;

    public TaskService(TaskMapper taskMapper, StaticRuleMapper staticRuleMapper, SqlSessionWrapper sqlSessionWrapper, DynamicRuleMapper dynamicRuleMapper, Counter counter, CommandMapper commandMapper) {
        this.taskMapper = taskMapper;
        this.staticRuleMapper = staticRuleMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.dynamicRuleMapper = dynamicRuleMapper;
        this.counter = counter;
        this.commandMapper = commandMapper;
    }

    @Transactional
    public Long newTask(Task task) {
        // todo: 目前获取方式还不确定，以后再确定
//        task.setTaskCreateUserId(1);
//        task.setTaskCreateUsername("xxx");
//        task.setTaskCreateDepart("xxx");

//        eventTypeValid(task);


        task.setTaskDisplayId(
                "RW-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-"
                + String.format("%06d", counter.generateId("task")));

        taskMapper.newTask(task);
        if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty()) {
            staticRuleMapper.queryStaticRuleByIds(task.getStaticRuleIds()).forEach(staticRuleObject -> {
                if (!staticRuleObject.getAuditStatus().equals(AuditStatusEnum.AUDITED.getNum())) {
                    throw new IllegalArgumentException("部分规则审批状态错误");
                }
                if (staticRuleObject.getStaticRuleUsedTaskId() != null) {
                    throw new IllegalArgumentException("部分静态规则已被其他任务使用");
                }
            });
            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds(), AuditStatusEnum.USING.getNum());
        }

        if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty()) {
            dynamicRuleMapper.queryDynamicRuleByIds(task.getDynamicRuleIds()).forEach(dynamicRuleObject -> {
                if (!dynamicRuleObject.getAuditStatus().equals(AuditStatusEnum.AUDITED.getNum())) {
                    throw new IllegalArgumentException("部分规则审批状态错误");
                }

                if (dynamicRuleObject.getDynamicRuleUsedTaskId() != null) {
                    throw new IllegalArgumentException("部分动态规则已被其他任务使用");
                }
            });
            taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds(), AuditStatusEnum.USING.getNum());
        }
        if (task.getTaskType() != TaskTypeEnum.STATIC.getTaskType()) {
            if (task.getProtectObjectIds() != null && !task.getProtectObjectIds().isEmpty()) {
                //校验防护对象是否存在
                boolean ProtectObjIdValid = task.getProtectObjectIds().stream()
                        .allMatch(dynamicRuleMapper::queryProtectObjectById);
                if (!ProtectObjIdValid) {
                    throw new IllegalArgumentException("部分防护对象不存在");
                }
                //任务和防护对象多对多关联建立
                taskMapper.newTaskProtectObjectConcat(task.getTaskId(), task.getProtectObjectIds());

            }
        }
        insertTaskStatusLog(task.getTaskId());
        return task.getTaskId();
    }

    /**
     * 更新任务关联的静态规则审批状态，用于任务新建/停止时候，修改审批状态为已使用/已审批，不能用于其他审批状态修改
     * @param taskId 与静态规则关联的任务ID
     * @param newAuditStatus 需要修改的审批状态
     */
    public void updateStaticRuleAuditStatusInTask(Long taskId, AuditStatusEnum newAuditStatus) {
        if (taskId == null) {
            return;
        }

        // 限制该函数仅能用于将规则修改为已审批/使用中
        if (!List.of(AuditStatusEnum.AUDITED, AuditStatusEnum.USING).contains(newAuditStatus)) {
            return;
        }

        List<Integer> staticRuleIds = taskMapper.queryStaticRuleIdsFromTaskId(taskId,
                List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum()));
        if (staticRuleIds == null || staticRuleIds.isEmpty()) {
            return;
        }

        List<StaticRuleObject> staticRuleObjects = staticRuleMapper.queryStaticRuleByIds(staticRuleIds);
        if (staticRuleObjects == null || staticRuleObjects.isEmpty()) {
            throw new IllegalArgumentException("静态规则列表中的ID不存在，请检查静态规则是否真实存在");
        }

        // 检查所有的静态规则审批状态是否正确，如果不正确则报错
        staticRuleObjects.forEach(staticRuleObject -> staticRuleObject.checkAuditStatusValidate(newAuditStatus));

        Map<Integer, Integer> staticRuleAuditStatusBatch = staticRuleObjects
                .stream()
                .collect(Collectors.toMap(
                        StaticRuleObject::getStaticRuleId,
                        k -> newAuditStatus.getNum(), // 将审核状态全部修改为使用中状态
                        (existing, replacement) -> existing)); // 如果有重复字段，默认使用先前值

        sqlSessionWrapper.startBatchSession(
                StaticRuleMapper.class,

                (Function<StaticRuleMapper, Function<Map<Integer, Integer>, Void>>) mapper -> staticRuleBatch -> {
                    Map<Integer, Integer> batchMap = MapUtils.newHashMapWithExpectedSize(BATCH_SIZE);
                    for (Map.Entry<Integer, Integer> auditStatusEntry : staticRuleBatch.entrySet()) {
                        batchMap.put(auditStatusEntry.getKey(), auditStatusEntry.getValue());
                        if (batchMap.size() < BATCH_SIZE) {
                            continue;
                        }

                        mapper.updateAuditStatusByIdBatch(batchMap);
//                        insertStaticRuleStatusLog(batchMap);
                        batchMap.clear();
                    }

                    mapper.updateAuditStatusByIdBatch(batchMap);
//                    insertStaticRuleStatusLog(batchMap);
                    batchMap.clear();

                    return null;
                },

                staticRuleAuditStatusBatch
        );
    }

    /**
     * 更新任务关联的动态规则审批状态，用于任务新建/停止时候，修改审批状态为已使用/已审批，不能用于其他审批状态修改
     * @param taskId 与动态规则关联的任务ID
     * @param newAuditStatus 需要修改的审批状态
     */
    public void updateDynamicRuleAuditStatusInTask(Long taskId, AuditStatusEnum newAuditStatus) {
        if (taskId == null) {
            return;
        }

        // 限制该函数仅能用于将规则修改为已审批/使用中
        if (!List.of(AuditStatusEnum.AUDITED, AuditStatusEnum.USING).contains(newAuditStatus)) {
            return;
        }

        List<Integer> dynamicRuleIds = taskMapper.queryDynamicRuleIdsFromTaskId(taskId,
                List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum()));
        if (dynamicRuleIds == null || dynamicRuleIds.isEmpty()) {
            return;
        }

        List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleMapper.queryDynamicRuleByIds(dynamicRuleIds);
        if (dynamicRuleObjects == null || dynamicRuleObjects.isEmpty()) {
            throw new IllegalArgumentException("动态规则列表中的ID不存在，请检查动态规则是否真实存在");
        }

        // 检查所有的动态规则列表的审批状态是否正确，如不正确则报错
        dynamicRuleObjects.forEach(dynamicRuleObject -> dynamicRuleObject.checkAuditStatusValidate(newAuditStatus));

        Map<Integer, Integer> dynamicRuleAuditStatusBatch = dynamicRuleObjects
                .stream()
                .collect(Collectors.toMap(
                        DynamicRuleObject::getDynamicRuleId,
                        k -> newAuditStatus.getNum(),
                        (existing, replacement) -> existing));

        sqlSessionWrapper.startBatchSession(
                DynamicRuleMapper.class,

                (Function<DynamicRuleMapper, Function<Map<Integer, Integer>, Void>>) mapper -> batch -> {
                    Map<Integer, Integer> batchMap = MapUtils.newHashMapWithExpectedSize(BATCH_SIZE);
                    for (Map.Entry<Integer, Integer> auditStatusEntry : batch.entrySet()) {
                        batchMap.put(auditStatusEntry.getKey(), auditStatusEntry.getValue());
                        if (batchMap.size() < BATCH_SIZE) {
                            continue;
                        }

                        mapper.updateAuditStatusByIdBatch(batchMap);
                        insertDynamicRuleStatusLog(batchMap);
                        batchMap.clear();
                    }

                    mapper.updateAuditStatusByIdBatch(batchMap);
                    insertDynamicRuleStatusLog(batchMap);
                    batchMap.clear();

                    return null;
                },

                dynamicRuleAuditStatusBatch
        );
    }

    @Transactional
    public List<Task> queryTasks(Integer taskStatus,
                                 Integer taskType, String taskName, String taskCreator,
                                 Integer auditStatus,
                                 String taskAct, String taskAuditor,
                                 String taskSource, String ruleName,
                                 String eventType,String createDateStr, String startDateStr,
                                 Integer protectLevel,
                                 Integer page, Integer pageSize) {

        List<Task> tasks = taskMapper.queryTasks(taskStatus, taskType, taskName, taskCreator, auditStatus,
                taskAct, taskAuditor, taskSource, ruleName,eventType, createDateStr, startDateStr,protectLevel, page, pageSize);
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            List<ProtectObject> protectObjects = taskMapper.queryProtectObjectsByTaskId(task.getTaskId());
            task.setProtectObjects(protectObjects);

            task.setStaticRuleIds(taskMapper.queryStaticRuleIdsFromTaskId(task.getTaskId(),
                    List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));
            task.setDynamicRuleIds(taskMapper.queryDynamicRuleIdsFromTaskId(task.getTaskId(),
                    List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));
        }

        return tasks;
    }

    @Transactional
    public Task queryTask(Long id) {
        Task task = taskMapper.queryTask(id);
        if (task == null) {
            return null;
        }
        List<ProtectObject> protectObjects = taskMapper.queryProtectObjectsByTaskId(id);
        task.setProtectObjects(protectObjects);

        task.setStaticRuleIds(taskMapper.queryStaticRuleIdsFromTaskId(task.getTaskId(),
                List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));
        task.setDynamicRuleIds(taskMapper.queryDynamicRuleIdsFromTaskId(task.getTaskId(),
                List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));

        return task;
    }

    @Transactional
    public Boolean updateTask(Task task) {
        //未审核不能修改
        if (!Objects.equals(taskMapper.queryTaskAuditStatus(task.getTaskId()), AuditStatusEnum.AUDITED.getNum())) {
            return false;
        }

        task.setTaskAuditStatus(AuditStatusEnum.PENDING.getNum());

        //校验防护对象是否存在
        boolean ProtectObjIdValid = task.getProtectObjectIds().stream()
                .allMatch(dynamicRuleMapper::queryProtectObjectById);
        if (!ProtectObjIdValid) {
            throw new IllegalArgumentException("部分防护对象不存在");
        }

        //删除task关联的protectObjects
        taskMapper.deleteTaskProtectObjectConcat(task.getTaskId());
        //更新task
        taskMapper.updateTask(task);
        //重新关联task和protectObjects
        taskMapper.newTaskProtectObjectConcat(task.getTaskId(), task.getProtectObjectIds());

        taskMapper.clearTaskConnectedStaticRule(task.getTaskId(), AuditStatusEnum.AUDITED.getNum());
        taskMapper.clearTaskConnectedDynamicRule(task.getTaskId(), AuditStatusEnum.AUDITED.getNum());

        if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty())
            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds(), AuditStatusEnum.USING.getNum());

        if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty())
            taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds(),AuditStatusEnum.USING.getNum());

        return true;
    }

    @Transactional
    public Boolean changeTaskAuditStatus(Long taskId, Integer taskAuditStatus,
                                         String auditUserName, String auditUserId, String auditUserDepart) {
        Integer originalAuditStatus = taskMapper.queryTaskAuditStatus(taskId);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("无法找到任务ID为" + taskId + "的任务，也许任务不存在？");
        }

        if (AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(taskAuditStatus))
            taskMapper.changeTaskAuditStatusWithAudior(taskId, taskAuditStatus, auditUserName,  auditUserId,  auditUserDepart);
        else return false;
        insertTaskStatusLog(taskId);
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
        insertTaskStatusLog(taskId);

        return true;
    }

    public Boolean deleteTask(Long taskId) {
        Task task = taskMapper.queryTask(taskId);
        if (task == null) {
            return true;
        }
//有的规则被任务选择了，但任务还没有启动，此时规则的状态不是已使用，但used_task_id已经被设置
        //所以应该选择任务的时候就
        updateStaticRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        updateDynamicRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        taskMapper.clearTaskConnectedStaticRule(task.getTaskId(), AuditStatusEnum.AUDITED.getNum());
        taskMapper.clearTaskConnectedDynamicRule(task.getTaskId(), AuditStatusEnum.AUDITED.getNum());

        commandMapper.removeCommandsByTaskId(taskId);

        return taskMapper.deleteTask(taskId);
    }


    public Boolean changeTaskStatus(Long taskId, Integer stateNum) {
        return taskMapper.changeTaskStatus(taskId, stateNum);
    }

    public List<TaskCommandInfo> getStaticCommandInfos(Long taskId) {
        List<TaskCommandInfo> staticCommandInfos = taskMapper.getStaticCommandInfos(taskId);

        staticCommandInfos.forEach(taskCommandInfo -> {
//            taskCommandInfo.setProtocolNum();
            if (taskCommandInfo.getFiveTupleWithMask().getProtocol() != null) {
                taskCommandInfo.getFiveTupleWithMask().setProtocolNum(Integer.valueOf(taskCommandInfo.getFiveTupleWithMask().getProtocol()));
            }
//            taskCommandInfo.setMask();
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

    public Integer queryTaskTotalNum(Integer taskStatus, Integer taskType, String taskName, String taskCreator, Integer auditStatus
    ,String taskAct, String taskAuditor, String taskSource, String ruleName,
                                     String eventType, String createDate, String startDate,Integer protectLevel) {
        return taskMapper.queryTaskTotalNum(taskStatus, taskType, taskName, taskCreator, auditStatus,
                taskAct, taskAuditor, taskSource, ruleName,null, eventType, createDate, startDate, protectLevel);
    }

    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = taskMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("任务id部分不存在");
        }

        int index = 0;
        List<Integer> errorIds = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            Integer originalAuditStatus = originalAuditStatusList.get(index);
            index++;

            if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()){
            throw new IllegalArgumentException("动态规则id无法修改为对应审核状态, errorIds: " + errorIds);
        }

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
                        //记录状态日志
                        insertTaskStatusLog(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        insertTaskStatusLog(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(TaskMapper.class, updateTaskAuditStatusFunction, idsWithAuditStatusMap);


    }

    public Boolean updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap,
                                          String auditUserName, String auditUserId, String auditUserDepart) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = taskMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            throw new IllegalArgumentException("任务id部分不存在");
        }

        int index = 0;
        List<Integer> errorIds = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            Integer originalAuditStatus = originalAuditStatusList.get(index);
            index++;

            if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()){
            throw new IllegalArgumentException("动态规则id无法修改为对应审核状态, errorIds: " + errorIds);
        }

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
                        mapper.updateAuditStatusWithAuditorByIdBatch(idWithAuditStatusBatch, auditUserName,  auditUserId,  auditUserDepart);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusWithAuditorByIdBatch(idWithAuditStatusBatch, auditUserName,  auditUserId,  auditUserDepart);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(TaskMapper.class, updateTaskAuditStatusFunction, idsWithAuditStatusMap);


    }

    public Integer queryAuditTaskTotalNum(Integer auditState) {
        return taskMapper.queryAuditTaskTotalNum(auditState);
    }

    public List<Integer> queryAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        return taskMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
    }

    public Boolean updateAuditInfo(List<Long> ids, String auditInfo) {
        return taskMapper.updateAuditInfo(ids, auditInfo);
    }

    public String queryAuditInfo(Integer id) {
        return taskMapper.queryAuditInfo(id);
    }

    public void insertTaskStatusLog(Long taskId) {
        taskMapper.updateTaskStatusLogExpireTime(taskId);
        taskMapper.insertTaskStatusLog(taskId);
    }
    public void insertTaskStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> taskIds = new ArrayList<>(keys);

        taskMapper.updateTaskStatusLogExpireTimeBatch(taskIds);
        taskMapper.insertTaskStatusLogBatch(taskIds);
    }

    public List<Task> queryHistory(Long id, Integer page, Integer pageSize) {
        List<Task> tasks =  taskMapper.queryHistory(id, page, pageSize);

        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            List<ProtectObject> protectObjects = taskMapper.queryProtectObjectsByTaskId(task.getTaskId());
            task.setProtectObjects(protectObjects);

            task.setStaticRuleIds(taskMapper.queryStaticRuleIdsFromTaskId(task.getTaskId(),
                    List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));
            task.setDynamicRuleIds(taskMapper.queryDynamicRuleIdsFromTaskId(task.getTaskId(),
                    List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum())));
        }
        return tasks;
    }

    public void removeDynamicRuleUsedTaskIdInTask(Long taskId) {
        dynamicRuleMapper.removeUsedTaskId(taskId);
    }

    public void removeStaticRuleUsedTaskIdInTask(Long taskId) {
        staticRuleMapper.removeUsedTaskId(taskId);
    }

    public void insertStaticRuleStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> ids = new ArrayList<>(keys);

//        staticRuleMapper.updateStaticRuleStatusLogExpireTimeBatch(ids);
        staticRuleMapper.insertStaticRuleStatusLogBatch(ids);
    }
    public void insertDynamicRuleStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> ids = new ArrayList<>(keys);

        dynamicRuleMapper.updateStatusLogExpireTimeBatch(ids);
        dynamicRuleMapper.insertStatusLogBatch(ids);

    }

    public List<Long> getRunnableTasks() {
        return taskMapper.queryRunnableTasks(StateEnum.PENDING.getStateNum(),AuditStatusEnum.AUDITED.getNum());
    }

    public void updateTaskStartTime(Long taskId) {
        taskMapper.updateTaskStartTime(taskId);
    }

    public void eventTypeValid(Task task) {
        if (task.getAuditUserDepartCode() != null) {

            String departments = taskMapper.queryEventTypeDepartment(task.getEventType());
            Boolean isOrgCodeValid = false;
            String[] departmentArray = departments.split(",");
            for (String department : departmentArray) {
                if (department.equals(task.getAuditUserDepartCode())) {
                    isOrgCodeValid = true;
                    break;
                }
            }
            if (!isOrgCodeValid) {
                throw new IllegalArgumentException("该用户所属部门不允许新建该事件类型的任务");
            }
        }
        Boolean isActValid = false;
        String acts = taskMapper.queryEventTypeAct(task.getEventType());
        String[] actArray = acts.split(",");
        for (String act : actArray) {
            if (act.equals(task.getTaskAct())) {
                isActValid = true;
                break;
            }
        }
        if (!isActValid) {
            throw new IllegalArgumentException("该事件类型不允许该处置动作");
        }


    }

    @Transactional
    public List<Task> auditInfoNotification(String userId) {
        List<Task> tasksNotification = taskMapper.queryAuditInfoNotification(userId,AuditStatusEnum.RETURNED.getNum());
        if (tasksNotification.isEmpty()){
            return tasksNotification;
        }
        List<Long> taskIdList = tasksNotification.stream().map(Task::getTaskId).toList();
        taskMapper.updateNotificationByTaskId(taskIdList);

        return tasksNotification;

    }

    public void chongtuValid(Task task) {
        if (task.getTaskAct().equals("23") &&
            task.getEventType().equals("APT") &&
            task.getTaskType()==1
        ){
            throw new IllegalArgumentException("任务冲突，任务类型：静态。任务动作：阻断+23（丢弃重定向）" +
                    "事件类型：APT攻击 ，开始时间"+task.getTaskStartTime()+"结束时间："+task.getTaskEndTime());
        }

    }

    public List<File> selectFilesByStaticRuleId(Long taskId) {
        return taskMapper.selectFilesByStaticRuleId(taskId);
    }
    public void saveFile(File saveFile) {
        taskMapper.saveFile(saveFile);
    }

    public File selectFileById(Integer id) {
        return taskMapper.selectFileById(id);
    }


}
