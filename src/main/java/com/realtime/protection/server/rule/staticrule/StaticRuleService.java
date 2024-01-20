package com.realtime.protection.server.rule.staticrule;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.status.AuditStatusValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class StaticRuleService {

    private final StaticRuleMapper staticRuleMapper;
    private final SqlSessionWrapper sqlSessionWrapper;

    public StaticRuleService(StaticRuleMapper staticRuleMapper, SqlSessionWrapper sqlSessionWrapper) {

        this.staticRuleMapper = staticRuleMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }


    /*
    新建静态规则
     */
    public Integer newStaticRuleObject(StaticRuleObject object) {

        object.setStaticRuleCreateTime(LocalDateTime.now());
        object.setStaticRuleAuditStatus(0);
        /*
        待开发：设置静态规则对象的创建用户、用户所属部门等属性
         */
        staticRuleMapper.newStaticRuleObject(object);

        return object.getStaticRuleId();
    }

    public Object deleteStaticRules(List<Integer> staticRuleIds) {
        //判断当前静态规则是否能够删除---是否存在任务选择的静态规则？？

        //删除静态规则
//        for (Integer id : staticRuleIds) {
//            staticRuleMapper.deleteStaticRule(id);
//        }
        Function<StaticRuleMapper, Function<List<Integer>, Boolean>> deleteStaticRulesFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<Integer> staticRuleBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (Integer staticRuleId : list) {
                        staticRuleBatch.add(staticRuleId);
                        if (staticRuleBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.deleteStaticRules(staticRuleBatch);
                        staticRuleBatch.clear();
                    }
                    if (!staticRuleBatch.isEmpty()) {
                        mapper.deleteStaticRules(staticRuleBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, deleteStaticRulesFunction, staticRuleIds);
    }

    public Integer updateStaticRule(Integer id, StaticRuleObject object) {
        //判断当前静态规则是否能够修改---是否存在任务选择的静态规则？？
        //按id查询该静态规则的used_task_id字段，如果不为空，则不能修改
        object.setStaticRuleModifyTime(LocalDateTime.now());
        //修改静态规则
        return staticRuleMapper.updateStaticRule(id, object);
    }

    public StaticRuleObject queryStaticRuleById(Integer id) {

        return staticRuleMapper.queryStaticRuleById(id);
    }

    /*
    分页查询
     */
    public List<StaticRuleObject> queryStaticRule(
            String static_rule_name, Integer static_rule_id,
            String creator, String ip,
            Integer page, Integer pageSize) {

        return staticRuleMapper.queryStaticRule(static_rule_name, static_rule_id,
                creator, ip, page, pageSize);
    }

    public Boolean newStaticRuleObjects(List<StaticRuleObject> staticRuleList) {
        Function<StaticRuleMapper, Function<List<StaticRuleObject>, Boolean>> newStaticRuleFunction =
                mapper -> list -> {
                    if (list == null || list.isEmpty()) {
                        return false;
                    }

                    List<StaticRuleObject> StaticRuleBatch = ListUtils.newArrayListWithExpectedSize(100);
                    for (StaticRuleObject staticRule : staticRuleList) {
                        staticRule.setStaticRuleCreateTime(LocalDateTime.now());
                        StaticRuleBatch.add(staticRule);
                        if (StaticRuleBatch.size() < 100) {
                            continue;
                        }
                        //mapper指的就是外层函数输入的参数，也就是WhiteListMapper
                        mapper.newStaticRules(StaticRuleBatch);
                        StaticRuleBatch.clear();
                    }
                    if (!StaticRuleBatch.isEmpty()) {
                        mapper.newStaticRules(StaticRuleBatch);
                    }
                    return true;
                };
        //实现事务操作
        return sqlSessionWrapper.startBatchSession(StaticRuleMapper.class, newStaticRuleFunction, staticRuleList);


    }

    @Transactional
    public Map<String, Object> updateAuditStatus(Integer id, Integer auditStatus) {
        Integer originalAuditStatus = staticRuleMapper.queryAuditStatusById(id);
        if (originalAuditStatus == null) {
            throw new IllegalArgumentException("cannot find audit status of static rule " + id + ", maybe static rule doesn't exist?");
        }
        if (!AuditStatusValidator.setOriginal(originalAuditStatus).checkValidate(auditStatus)) {
            throw new IllegalArgumentException("invalid audit status");
        }
        Boolean success = staticRuleMapper.updateAuditStatusById(id, auditStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", success);
        resultMap.put("audit_status", auditStatus);
        return resultMap;
    }

    public void deleteStaticRuleById(Integer id) {
        staticRuleMapper.deleteStaticRuleById(id);
    }

    public Integer queryStaticRuleTotalNum() {
        return staticRuleMapper.queryStaticRuleTotalNum();
    }
}
