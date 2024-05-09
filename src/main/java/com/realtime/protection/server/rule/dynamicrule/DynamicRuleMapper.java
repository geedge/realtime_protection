package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicRuleMapper {

    //新建动态规则
    void newDynamicRule(@Param("object") DynamicRuleObject dynamicRuleObject);

    //新建动态规则与保护对象关联
    Boolean newDynamicRulProtectObjectConcat(long dynamicRuleId, Integer protectObjectId);

    void deleteDynamicRuleObject(Integer dynamicRuleId);

    DynamicRuleObject queryDynamicRuleById(Integer dynamicRuleId);

    List<ProtectObject> queryProtectObjectByRuleId(Integer dynamicRuleId);

    Boolean updateDynamicRuleObject(@Param("dynamicRuleId") Integer dynamicRuleId, @Param("object") DynamicRuleObject dynamicRuleObject);

    void newDynamicRules(List<DynamicRuleObject> dynamicRuleObjects);

    void deleteDynamicRules(List<Integer> dynamicRuleIds);

    List<DynamicRuleObject> queryDynamicRuleObject(String dynamicRuleName, Integer dynamicRuleId,
                                                   String sourceSystem, String creator, Integer auditStatus,
                                                   String eventType, Integer protectLevel, String templateName,
                                                   Integer page, Integer pageSize);

    void deleteDynamicRuleProtectObjectConcat(Integer dynamicRuleId);

    Template queryTemplateByRuleId(Integer dynamicRuleId);

    boolean newDynamicRulProtectObjectsConcat(Integer dynamicRuleId, List<Integer> protectObjectIds);

    boolean queryProtectObjectById(Integer protectObjectId);

    Integer queryDynamicRuleTotalNum(String dynamicRuleName, Integer dynamicRuleId,
                                     String sourceSystem, String creator, Integer auditStatus
                                    , String eventType, Integer protectLevel, String templateName);

    List<DynamicRuleObject> queryDynamicRuleByIds(List<Integer> ids);

    Integer queryTaskStatusBydynamicRuleId(Integer dynamicRuleId);

    Integer queryUsedDynamicRuleTotalNum();

    Integer queryAuditDynamicRuleTotalNum(int auditStatus);

    Integer queryAuditStatusById(Integer dynamicRuleId);

    Boolean updateAuditStatusById(Integer dynamicRuleId, Integer auditStatus);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    List<Integer> queryAuditStatusByIds(@Param("idWithAuditStatusMap") Map<Integer, Integer> idWithAuditStatusMap);

    Boolean updateAuditInfo(@Param("ids")List<Integer> ids, @Param("auditInfo")String auditInfo);

    String queryAuditInfo(Integer id);
}
