package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StaticRuleMapper {
    Integer newStaticRuleObject(@Param("object") StaticRuleObject object);

}
