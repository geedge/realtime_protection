package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
