package com.realtime.protection.configuration.utils.enums;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.utils.NonEmptyFieldFetcher;

import java.lang.reflect.Field;
import java.util.*;

public enum RuleEnum {
    // 不带掩码规则的五元组规则类型对应表
    SIP_SPORT_DIP_PROTOCOL("SIP_SPORT_DIP_PROTOCOL"),
    SIP_DIP_DPORT_PROTOCOL("SIP_DIP_DPORT_PROTOCOL"),
    SIP_SPORT_PROTOCOL("SIP_SPORT_PROTOCOL"),
    SIP_DIP_PROTOCOL("SIP_DIP_PROTOCOL"),
    SIP_DPORT_PROTOCOL("SIP_DPORT_PROTOCOL"),
    SPORT_DIP_PROTOCOL("SPORT_DIP_PROTOCOL"),
    DIP_DPORT_PROTOCOL("DIP_DPORT_PROTOCOL"),
    SIP_DIP("SIP_DIP"),
    SIP_PROTOCOL("SIP_PROTOCOL"),
    DIP_PROTOCOL("DIP_PROTOCOL"),
    SIP("SIP"),
    DIP("DIP"),
    // 带掩码规则的五元组规则类型对应表
    DIP_MDIP("DIP_MDIP"),
    DIP_MDIP_PROTOCOL_MPROTOCOL("DIP_MDIP_PROTOCOL_MPROTOCOL"),
    DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL("DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL"),
    SPORT_MSPORT_DIP_MDIP_PROTOCOL_MPROTOCOL("SPORT_MSPORT_DIP_MDIP_PROTOCOL_MPROTOCOL"),
    SPORT_MSPORT_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL("SPORT_MSPORT_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL"),
    SIP_MSIP("SIP_MSIP"),
    SIP_MSIP_PROTOCOL_MPROTOCOL("SIP_MSIP_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL("SIP_MSIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_SPORT_MSPORT_PROTOCOL_MPROTOCOL("SIP_MSIP_SPORT_MSPORT_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_SPORT_MSPORT_DPORT_MDPORT_PROTOCOL_MPROTOCOL("SIP_MSIP_SPORT_MSPORT_DPORT_MDPORT_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_DIP_MDIP("SIP_MSIP_DIP_MDIP"),
    SIP_MSIP_DIP_MDIP_PROTOCOL_MPROTOCOL("SIP_MSIP_DIP_MDIP_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL("SIP_MSIP_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_SPORT_MSPORT_DIP_MDIP_PROTOCOL_MPROTOCOL("SIP_MSIP_SPORT_MSPORT_DIP_MDIP_PROTOCOL_MPROTOCOL"),
    SIP_MSIP_SPORT_MSPORT_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL("SIP_MSIP_SPORT_MSPORT_DIP_MDIP_DPORT_MDPORT_PROTOCOL_MPROTOCOL")
    ;

    private final Set<String> rule;
    private static final List<Set<String>> ruleEnumList = new ArrayList<>();

    static {
        for (RuleEnum ruleEnum : RuleEnum.values()) {
            ruleEnumList.add(ruleEnum.rule);
        }
    }

    RuleEnum(String ruleName) {
        this.rule = new HashSet<>(Arrays.stream(ruleName.split("_")).toList());
    }

    public static Boolean checkValidate(StaticRuleObject staticRuleObject) throws IllegalAccessException {
        List<Field> nonEmptyFields = NonEmptyFieldFetcher.getNonEmptyFields(staticRuleObject);
        List<String> fieldNames = new java.util.ArrayList<>(List.of());
        for (Field field : nonEmptyFields) {
            String fieldName = field.getName().toUpperCase().replace("STATICRULE", "");
            // 将合理的非空字段加入fieldNames中
            // 需要去掉STATICRULE字段，仅保留后面的字段
            if (fieldName.contains("IP")
                    || fieldName.contains("PORT")
                    || fieldName.contains("PROTOCOL")) fieldNames.add(fieldName);
        }
        return ruleEnumList.stream().anyMatch(rule -> rule.equals(new HashSet<>(fieldNames)));
    }
}
