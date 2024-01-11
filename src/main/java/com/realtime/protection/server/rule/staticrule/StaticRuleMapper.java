package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StaticRuleMapper {

    //新建静态规则
    void newStaticRuleObject(@Param("object") StaticRuleObject object);

    //根据主键删除静态规则
    @Delete("delete from t_static_rule where static_rule_id = #{id}")
    Boolean deleteStaticRuleById(Integer id);


    //修改静态规则
    void updateStaticRule(StaticRuleObject object);

    //按id查询静态规则
    //@Select("select * from t_static_rule where static_rule_id = #{id}")
    StaticRuleObject queryStaticRuleById(Integer id);

    //多页查询静态规则
    List<StaticRuleObject> queryStaticRule(String static_rule_name, Integer static_rule_id,
                                           Integer page, Integer pageSize);


    void deleteStaticRules(@Param("whiteListIds") List<Integer> staticRuleBatch);

    void newStaticRules(List<StaticRuleObject> staticRuleBatch);
}
