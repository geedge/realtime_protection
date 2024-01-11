package com.realtime.protection.server.rule.dynamicrule;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        //不需要使用 join
        //在数据库中设置了级联删除 ON DELETE CASCADE，在删除在从父表中删除数据时自动删除子表中的数据
        dynamicRuleMapper.deleteDynamicRuleObject(dynamicRuleId);

    }

    @Transactional
    public DynamicRuleObject queryDynamicRuleById(Integer dynamicRuleId) {

        //查询DynamicRule
        DynamicRuleObject dynamicRuleObject = dynamicRuleMapper.queryDynamicRuleById(dynamicRuleId);
        //查询DynamicRule关联的ProtectObject
        dynamicRuleObject.setProtectObjects(dynamicRuleMapper.queryProtectObjectByRuleId(dynamicRuleId));

        return dynamicRuleObject;
    }

    @Transactional
    public void updateDynamicRuleObject(Integer dynamicRuleId, DynamicRuleObject dynamicRuleObject) {
        //更新DynamicRule
        dynamicRuleObject.setDynamicRuleModifyTime(LocalDateTime.now());
        dynamicRuleMapper.updateDynamicRuleObject(dynamicRuleId, dynamicRuleObject);
        if (dynamicRuleObject.getProtectObjectIds() == null || dynamicRuleObject.getProtectObjectIds().isEmpty()) {
            return;
        }
        //删除DynamicRule关联的ProtectObject
        dynamicRuleMapper.deleteDynamicRuleProtectObjectConcat(dynamicRuleId);
        //新增DynamicRule关联的ProtectObject
        dynamicRuleObject.getProtectObjectIds().forEach(
                protectObjectId -> dynamicRuleMapper.newDynamicRulProtectObjectConcat(dynamicRuleId, protectObjectId));
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
    public List<DynamicRuleObject> queryDynamicRuleObject(String dynamicRuleName, Integer dynamicRuleId, Integer page, Integer pageSize) {
        return dynamicRuleMapper.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId, page, pageSize);
    }
}
