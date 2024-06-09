package com.realtime.protection.server.rule.dynamicrule;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class DynamicRuleService {
    private final DynamicRuleMapper dynamicRuleMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private final Counter counter;

    public DynamicRuleService(DynamicRuleMapper dynamicRuleMapper, SqlSessionWrapper sqlSessionWrapper, Counter counter) {
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.dynamicRuleMapper = dynamicRuleMapper;
        this.counter = counter;
    }

    @Transactional
    public Integer newDynamicRuleObject(DynamicRuleObject dynamicRule) {

        dynamicRule.setDynamicRuleDisplayId(
                "DTGZ-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-"
                + String.format("%06d", counter.generateId("dynamic_rule"))
        );

        dynamicRuleMapper.newDynamicRule(dynamicRule);
        insertStatusLog(dynamicRule.getDynamicRuleId());
        return dynamicRule.getDynamicRuleId();
    }

    //批量新建多个动态规则
    public Boolean newDynamicRuleObjects(List<DynamicRuleObject> DynamicRuleList) {
        Function<DynamicRuleMapper, Function<List<DynamicRuleObject>, Boolean>> newDynamicRuleFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<DynamicRuleObject> DynamicRuleIdBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (DynamicRuleObject dynamicRule : DynamicRuleList) {
                        dynamicRule.setDynamicRuleCreateTime(LocalDateTime.now());
                        dynamicRule.setDynamicRuleDisplayId(
                                "DTGZ-"
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                                + "-"
                                + String.format("%06d", counter.generateId("dynamic_rule"))
                        );
                        DynamicRuleIdBatch.add(dynamicRule);
                        if (DynamicRuleIdBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.newDynamicRules(DynamicRuleIdBatch);
                        DynamicRuleIdBatch.clear();
                    }
                    if (!DynamicRuleIdBatch.isEmpty()) {
                        mapper.newDynamicRules(DynamicRuleIdBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, newDynamicRuleFunction, DynamicRuleList);
    }


    public void deleteDynamicRuleObject(Integer dynamicRuleId) {
        //根据任务状态判断能否删除
        Integer taskStatus = dynamicRuleMapper.queryTaskStatusBydynamicRuleId(dynamicRuleId);
        if (taskStatus == null){
            dynamicRuleMapper.deleteDynamicRuleObject(dynamicRuleId);
        }else{
            switch (StateEnum.getStateEnumByNum(taskStatus)){
                case RUNNING:
                    throw new IllegalArgumentException("使用该动态规则的任务处于运行状态");
                case PAUSED:
                    throw new IllegalArgumentException("使用该动态规则的任务处于暂停状态");
                default:
                    break;
            }
            //不需要使用 join,在数据库中设置了级联删除 ON DELETE CASCADE，在删除在从父表中删除数据时自动删除子表中的数据
            dynamicRuleMapper.deleteDynamicRuleObject(dynamicRuleId);
        }


    }

    @Transactional
    public DynamicRuleObject queryDynamicRuleById(Integer dynamicRuleId) {

        //查询DynamicRule
        DynamicRuleObject dynamicRuleObject = dynamicRuleMapper.queryDynamicRuleById(dynamicRuleId);
        if (dynamicRuleObject == null){
            throw new IllegalArgumentException("动态规则不存在");
        }

        return dynamicRuleObject;
    }

    @Transactional
    public boolean updateDynamicRuleObject(Integer dynamicRuleId, DynamicRuleObject dynamicRuleObject) {
        //更新DynamicRule

//        dynamicRuleMapper
        dynamicRuleObject.setAuditStatus(AuditStatusEnum.PENDING.getNum());
        Boolean idValid = dynamicRuleMapper.updateDynamicRuleObject(dynamicRuleId, dynamicRuleObject);
        if (!idValid) {
            throw new IllegalArgumentException("动态规则id无效");
        }
        /*
        //删除DynamicRule关联的ProtectObject
        dynamicRuleMapper.deleteDynamicRuleProtectObjectConcat(dynamicRuleId);
        //新增DynamicRule关联的ProtectObject
        boolean ProtectObjIdValid = dynamicRuleObject.getProtectObjectIds().stream()
                .allMatch(dynamicRuleMapper::queryProtectObjectById);
        if (!ProtectObjIdValid) {
            throw new IllegalArgumentException("防护对象id无效");
        }

*/
        return idValid;

    }
    //    批量删除
    public Boolean deleteDynamicRuleObjects(List<Integer> dynamicRuleIds) {

        //根据是否处于已使用 审核状态 判断能否删除
        List<Integer> taskStatus = dynamicRuleMapper.queryAuditStatusByIdList(dynamicRuleIds);
        int index = 0;
        List<Integer> errorIds = new ArrayList<>();
        for(Integer status: taskStatus) {
            if (status == 3) {
                errorIds.add(dynamicRuleIds.get(index));
            }
            index++;
        }
        if (!errorIds.isEmpty()){
            throw  new IllegalArgumentException("动态规则使用中, 错误id:" + errorIds);
        }

        Function<DynamicRuleMapper, Function<List<Integer>, Boolean>> deleteDynamicRuleFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<Integer> DynamicRuleIdBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (Integer dynamicRuleId : dynamicRuleIds) {
                        DynamicRuleIdBatch.add(dynamicRuleId);
                        if (DynamicRuleIdBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.deleteDynamicRules(DynamicRuleIdBatch);
                        DynamicRuleIdBatch.clear();
                    }
                    if (!DynamicRuleIdBatch.isEmpty()) {
                        mapper.deleteDynamicRules(DynamicRuleIdBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, deleteDynamicRuleFunction, dynamicRuleIds);

    }


    //分页查询基础的动态规则，暂时不返回关联的保护对象 
    public List<DynamicRuleObject> queryDynamicRuleObject(String dynamicRuleName, String dynamicRuleId,
                                                          String templateSourceSystem,String creator,Integer auditStatus,
                                                          String eventType, Integer protectLevel, String templateName,
                                                          String auditUserName, String createDate,
                                                          Integer page, Integer pageSize) {
        return dynamicRuleMapper.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId,
                templateSourceSystem, creator, auditStatus, eventType, protectLevel, templateName, auditUserName, createDate, page, pageSize);
    }

    public Integer queryDynamicRuleTotalNum(String dynamicRuleName, String dynamicRuleId,
                                            String protectObjectSourceSystem, String creator,
                                            Integer auditStatus, String eventType, Integer protectLevel,
                                            String templateName,String auditUserName, String createDate) {
        return dynamicRuleMapper.queryDynamicRuleTotalNum(dynamicRuleName, dynamicRuleId,
                protectObjectSourceSystem, creator, auditStatus, eventType, protectLevel, templateName,auditUserName, createDate);
    }

    public List<DynamicRuleObject> queryDynamicRuleByIds(List<Integer> ids) {
        return dynamicRuleMapper.queryDynamicRuleByIds(ids);
    }

    public Integer queryUsedDynamicRuleTotalNum() {
        return  dynamicRuleMapper.queryUsedDynamicRuleTotalNum();
    }

    public Integer queryAuditDynamicRuleTotalNum(int auditStatus) {
        return  dynamicRuleMapper.queryAuditDynamicRuleTotalNum(auditStatus);
    }

    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus,
                                                 String auditUserName, Integer auditUserId, String auditUserDepart) {
        Integer originalAuditStatus = dynamicRuleMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("不能找到静态规则" + id );
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = dynamicRuleMapper.updateAuditStatusById(id, auditStatus ,auditUserName, auditUserId, auditUserDepart);

        insertStatusLog(id);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }


    public Object updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = dynamicRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            return new IllegalArgumentException("部分动态规则id不存在");
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
            return new IllegalArgumentException("动态规则id无法修改为对应审核状态, 错误id: " + errorIds);
        }


        Function<DynamicRuleMapper, Function<Map<Integer, Integer>, Boolean>> updateDynamicRuleAuditStatusFunction =
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
                        insertStatusLog(idWithAuditStatusBatch);
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                        insertStatusLog(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, updateDynamicRuleAuditStatusFunction, idsWithAuditStatusMap);

    }

    /**
     * 用于外部调用，更新审批用户信息
     * @param idsWithAuditStatusMap
     * @param auditUserName
     * @param auditUserId
     * @param auditUserDepart
     * @return
     */
    public Object updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap,
                                         String auditUserName, String auditUserId, String auditUserDepart) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = dynamicRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        if (originalAuditStatusList == null || originalAuditStatusList.size() != idsWithAuditStatusMap.size()) {
            return new IllegalArgumentException("部分动态规则id不存在");
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
            return new IllegalArgumentException("动态规则id无法修改为对应审核状态, 错误id: " + errorIds);
        }


        Function<DynamicRuleMapper, Function<Map<Integer, Integer>, Boolean>> updateDynamicRuleAuditStatusFunction =
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
                        insertStatusLog(idWithAuditStatusBatch);

                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusWithAuditorByIdBatch(idWithAuditStatusBatch, auditUserName,  auditUserId,  auditUserDepart);
                        insertStatusLog(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, updateDynamicRuleAuditStatusFunction, idsWithAuditStatusMap);

    }
    public List<Integer> queryAuditStatusByIds(Map<Integer, Integer> idsWithAuditStatusMap) {
        return dynamicRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
    }

    public Boolean updateAuditInfo(List<Integer> ids, String auditInfo) {
        return dynamicRuleMapper.updateAuditInfo(ids, auditInfo);
    }

    public String queryAuditInfo(Integer id) {
        return dynamicRuleMapper.queryAuditInfo(id);
    }

    public void insertStatusLog(Integer id) {

        dynamicRuleMapper.updateStatusLogExpireTime(id);
        dynamicRuleMapper.insertStatusLog(id);
    }
    public void insertStatusLog(Map<Integer, Integer> idWithAuditStatusBatch) {
        Set<Integer> keys = idWithAuditStatusBatch.keySet();
        ArrayList<Integer> ids = new ArrayList<>(keys);

        dynamicRuleMapper.updateStatusLogExpireTimeBatch(ids);
        dynamicRuleMapper.insertStatusLogBatch(ids);

    }

    public void insertSStatusLog(List<StaticRuleObject> objects) {
        List<Integer> ids = new ArrayList<>();
        for (StaticRuleObject staticRuleObject : objects) {
            ids.add(staticRuleObject.getStaticRuleId());
        }

        dynamicRuleMapper.updateStatusLogExpireTimeBatch(ids);
        dynamicRuleMapper.insertStatusLogBatch(ids);
    }


    public List<DynamicRuleObject> queryHistory(Integer id, Integer page, Integer pageSize) {
        return dynamicRuleMapper.queryHistory(id, page, pageSize);
    }
}
