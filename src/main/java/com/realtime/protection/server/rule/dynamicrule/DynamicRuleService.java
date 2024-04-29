package com.realtime.protection.server.rule.dynamicrule;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class DynamicRuleService {
    private final DynamicRuleMapper dynamicRuleMapper;
    private final SqlSessionWrapper sqlSessionWrapper;

    public DynamicRuleService(DynamicRuleMapper dynamicRuleMapper, SqlSessionWrapper sqlSessionWrapper) {

        this.sqlSessionWrapper = sqlSessionWrapper;
        this.dynamicRuleMapper = dynamicRuleMapper;
    }

    @Transactional
    public Integer newDynamicRuleObject(DynamicRuleObject dynamicRule) {

        //判断protectObject id是否有效
        boolean ProtectObjIdValid = dynamicRule.getProtectObjectIds().stream()
                .allMatch(dynamicRuleMapper::queryProtectObjectById);
        if (!ProtectObjIdValid) {
            throw new IllegalArgumentException("protect object id is invalid");
        }

        dynamicRuleMapper.newDynamicRule(dynamicRule);
        Integer dynamicRuleId = dynamicRule.getDynamicRuleId();
        dynamicRule.getProtectObjectIds().forEach(
                protectObjectId -> dynamicRuleMapper.newDynamicRulProtectObjectConcat(dynamicRuleId, protectObjectId));

        return dynamicRuleId;
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
            throw new IllegalArgumentException("dynamic rule id is invalid");
        }
        //查询DynamicRule关联的ProtectObject
        dynamicRuleObject.setProtectObjects(dynamicRuleMapper.queryProtectObjectByRuleId(dynamicRuleId));
        //查询DynamicRule关联的template详细信息
        Template template = dynamicRuleMapper.queryTemplateByRuleId(dynamicRuleId);
        if (template == null){
            //template在表中删除了，需要重新设置template（感觉这种情况不多见）
            dynamicRuleObject.setDynamicRuleSourceSystem(null);
            dynamicRuleObject.setDynamicRuleEventType(null);
        }else{
        dynamicRuleObject.setDynamicRuleSourceSystem(template.getSourceSystem());
        dynamicRuleObject.setDynamicRuleEventType(template.getTemplateName());
        }

        return dynamicRuleObject;
    }

    @Transactional
    public boolean updateDynamicRuleObject(Integer dynamicRuleId, DynamicRuleObject dynamicRuleObject) {
        //更新DynamicRule
        dynamicRuleObject.setDynamicRuleModifyTime(LocalDateTime.now());

        Boolean idValid = dynamicRuleMapper.updateDynamicRuleObject(dynamicRuleId, dynamicRuleObject);
        if (!idValid) {
            throw new IllegalArgumentException("dynamic rule id is invalid");
        }
        //删除DynamicRule关联的ProtectObject
        dynamicRuleMapper.deleteDynamicRuleProtectObjectConcat(dynamicRuleId);
        //新增DynamicRule关联的ProtectObject
        boolean ProtectObjIdValid = dynamicRuleObject.getProtectObjectIds().stream()
                .allMatch(dynamicRuleMapper::queryProtectObjectById);
        if (!ProtectObjIdValid) {
            throw new IllegalArgumentException("protect object id is invalid");
        }

        return dynamicRuleMapper.newDynamicRulProtectObjectsConcat(dynamicRuleId, dynamicRuleObject.getProtectObjectIds());

    }
    //    批量删除
    public Boolean deleteDynamicRuleObjects(List<Integer> dynamicRuleIds) {
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
    public List<DynamicRuleObject> queryDynamicRuleObject(String dynamicRuleName, Integer dynamicRuleId,
                                                          String templateSourceSystem,String creator,
                                                          Integer page, Integer pageSize) {
        return dynamicRuleMapper.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId,
                templateSourceSystem, creator, page, pageSize);
    }

    public Integer queryDynamicRuleTotalNum(String dynamicRuleName, Integer dynamicRuleId,
                                            String protectObjectSourceSystem, String creator) {
        return dynamicRuleMapper.queryDynamicRuleTotalNum(dynamicRuleName, dynamicRuleId,
                protectObjectSourceSystem, creator);
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

    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus) {
        Integer originalAuditStatus = dynamicRuleMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("cannot find audit status of static rule " + id + ", maybe static rule doesn't exist?");
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = dynamicRuleMapper.updateAuditStatusById(id, auditStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }

    public Object updateAuditStatusBatch(Map<Integer, Integer> idsWithAuditStatusMap) {
        //校验id和status是否合法
        List<Integer> originalAuditStatusList = dynamicRuleMapper.queryAuditStatusByIds(idsWithAuditStatusMap);
        int index = 0;
        List<Integer> errorIds = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            Integer originalAuditStatus = originalAuditStatusList.get(index);
            index++;
            if (originalAuditStatus == null) {
                errorIds.add(id);
            }
            if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()){
            return new IllegalArgumentException("动态规则id不存在或无法修改为对应审核状态, errorIds: " + errorIds);
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
                        idWithAuditStatusBatch.clear();
                    }
                    if (!idWithAuditStatusBatch.isEmpty()) {
                        mapper.updateAuditStatusByIdBatch(idWithAuditStatusBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(DynamicRuleMapper.class, updateDynamicRuleAuditStatusFunction, idsWithAuditStatusMap);

    }
}
