package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaticRuleService {
    @Autowired
    private StaticRuleMapper staticRuleMapper;


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

    public void deleteStaticRule(List<Integer> ids) {
        //判断当前静态规则是否能够删除---是否存在任务选择的静态规则？？

        //删除静态规则
        for (Integer id : ids) {
            staticRuleMapper.deleteStaticRule(id);
        }

    }

    public void updateStaticRule(StaticRuleObject object) {
        //判断当前静态规则是否能够修改---是否存在任务选择的静态规则？？
        //按id查询该静态规则的used_task_id字段，如果不为空，则不能修改
        object.setStaticRuleModifyTime(LocalDateTime.now());
        //修改静态规则
        staticRuleMapper.updateStaticRule(object);
    }

    public StaticRuleObject queryStaticRuleById(Integer id) {
        return staticRuleMapper.queryStaticRuleById(id);
    }

    /*
    分页查询
     */
    public List<StaticRuleObject> queryStaticRule(String static_rule_name, Integer static_rule_id, Integer page, Integer pageSize) {

        return staticRuleMapper.queryStaticRule(static_rule_name,static_rule_id,page,pageSize);
    }

}
