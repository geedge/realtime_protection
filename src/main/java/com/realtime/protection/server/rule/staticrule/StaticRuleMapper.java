package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StaticRuleMapper {

    //新建静态规则
    void newStaticRuleObject(@Param("object") StaticRuleObject object);

    //根据主键删除静态规则
    @Delete("delete from t_static_rule where static_rule_id = #{id}")
    void deleteStaticRuleById(Integer id);


    //修改静态规则
    Boolean updateStaticRule(@Param("id") Integer id, @Param("object") StaticRuleObject object);

    //按id查询静态规则
    //@Select("select * from t_static_rule where static_rule_id = #{id}")
    StaticRuleObject queryStaticRuleById(Integer id);

    //多页查询静态规则
    List<StaticRuleObject> queryStaticRule(String static_rule_name, String static_rule_id,
                                           String static_rule_create_username, String sip,
                                           Integer sport, String dip, Integer dport,
                                           Integer auditStatus, String auditUserName, String createDate,
                                           Integer page, Integer pageSize);


    void deleteStaticRules(@Param("staticRuleIds") List<Integer> staticRuleBatch);

    void newStaticRules(List<StaticRuleObject> list);

    Integer queryAuditStatusById(Integer id);

    Boolean updateAuditStatusByIdWithUser(Integer id, Integer auditStatus,
                                  String auditUserName,
                                  Integer auditUserId,
                                  String auditUserDepart);
    Boolean updateAuditStatusById(Integer id, Integer auditStatus);

    Integer queryStaticRuleTotalNum(String static_rule_name, String static_rule_id,
                                    String static_rule_create_username,
                                    String sip, Integer sport,
                                    String dip, Integer dport,
                                    Integer auditStatus,String auditUserName, String createDate);

    List<StaticRuleObject> queryStaticRuleByIds(List<Integer> ids);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch")Map<Integer, Integer> idWithAuditStatusBatch);
    void updateAuditStatusWithAuditorByIdBatch(@Param("idWithAuditStatusBatch")Map<Integer, Integer> idWithAuditStatusBatch,
                                    @Param("auditUserName")String auditUserName,
                                    @Param("auditUserId")Integer auditUserId,
                                    @Param("auditUserDepart")String auditUserDepart);
    Integer queryUsedStaticRuleTotalNum();

    Integer queryAuditStaticRuleTotalNum(@Param("auditStatus")Integer auditStatus);

    List<Integer> queryAuditStatusByIds(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idsWithAuditStatusMap);

    Boolean updateAuditInfo(@Param("ids") List<Integer> ids, @Param("auditInfo") String auditInfo);

    String queryAuditInfo(Integer id);

    void insertStaticRuleStatusLog(Integer id);

    void updateStaticRuleStatusLogExpireTime(Integer id);

    void insertStaticRuleStatusLogBatch(List<Integer> ids);

    void updateStaticRuleStatusLogExpireTimeBatch(List<Integer> ids);

    List<StaticRuleObject> queryHistory(Integer id, Integer page, Integer pageSize);

    void removeUsedTaskId(Long taskId);

//    boolean queryStaticRuleRepeat(StaticRuleObject object);
}
