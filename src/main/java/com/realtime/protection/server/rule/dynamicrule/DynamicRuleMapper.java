package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicRuleMapper {

    //新建动态规则
    void newDynamicRule(@Param("object") DynamicRuleObject dynamicRuleObject);

    void deleteDynamicRuleObject(Integer dynamicRuleId);

    DynamicRuleObject queryDynamicRuleById(Integer dynamicRuleId);


    Boolean updateDynamicRuleObject(@Param("dynamicRuleId") Integer dynamicRuleId, @Param("object") DynamicRuleObject dynamicRuleObject);

    void newDynamicRules(List<DynamicRuleObject> dynamicRuleObjects);

    void deleteDynamicRules(List<Integer> dynamicRuleIds);

    List<DynamicRuleObject> queryDynamicRuleObject(String dynamicRuleName, Integer dynamicRuleId,
                                                   String sourceSystem, String creator, Integer auditStatus,
                                                   String eventType, Integer protectLevel, String templateName,
                                                   String auditUserName, String createDate,
                                                   Integer page, Integer pageSize);

    boolean queryProtectObjectById(Integer protectObjectId);

    Integer queryDynamicRuleTotalNum(String dynamicRuleName, Integer dynamicRuleId,
                                     String sourceSystem, String creator, Integer auditStatus
                                    , String eventType, Integer protectLevel, String templateName,
                                     String auditUserName, String createDate);

    List<DynamicRuleObject> queryDynamicRuleByIds(List<Integer> ids);

    Integer queryTaskStatusBydynamicRuleId(Integer dynamicRuleId);

    Integer queryUsedDynamicRuleTotalNum();

    Integer queryAuditDynamicRuleTotalNum(int auditStatus);

    Integer queryAuditStatusById(Integer dynamicRuleId);

    Boolean updateAuditStatusById(Integer dynamicRuleId, Integer auditStatus,
                                  String auditUserName, Integer auditUserId, String auditUserDepart);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    List<Integer> queryAuditStatusByIds(@Param("idWithAuditStatusMap") Map<Integer, Integer> idWithAuditStatusMap);

    Boolean updateAuditInfo(@Param("ids")List<Integer> ids, @Param("auditInfo")String auditInfo);

    String queryAuditInfo(Integer id);

    void updateAuditStatusWithAuditorByIdBatch(@Param("idWithAuditStatusBatch")Map<Integer, Integer> idWithAuditStatusBatch,
                                               @Param("auditUserName")String auditUserName,
                                               @Param("auditUserId")String auditUserId,
                                               @Param("auditUserDepart")String auditUserDepart);

    void insertStatusLog(Integer id);

    void updateStatusLogExpireTime(Integer id);

    void insertStatusLogBatch(List<Integer> ids);

    void updateStatusLogExpireTimeBatch(List<Integer> ids);

    List<DynamicRuleObject> queryHistory(Integer id, Integer page, Integer pageSize);

    void removeUsedTaskId(Long taskId);
}
