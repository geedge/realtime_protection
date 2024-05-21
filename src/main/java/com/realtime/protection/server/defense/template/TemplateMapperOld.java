package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.defense.template.Template;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper()
public interface TemplateMapperOld {
    void newTemplate(@Param("template") Template template);

    void newProtectLevel(@Param("level") ProtectLevel protectLevel);

    List<Template> queryTemplates(@Param("template_name") String templateName,
                                  @Param("source_system") String sourceSystem,
                                  @Param("create_username") String createUsername,
                                  @Param("template_id")Integer templateId,
                                  @Param("audit_status")Integer auditStatus,
                                  @Param("page") Integer page,
                                  @Param("page_size") Integer pageSize);

    ProtectLevel queryProtectLevel(@Param("level_id") Integer protectLevelId);

    Boolean updateTemplateInformation(@Param("template") Template template);

    Template queryTemplate(@Param("template_id") Integer templateId);

    @Delete("DELETE FROM t_strategy_template WHERE strategy_template_id = #{template_id}")
    Boolean deleteTemplate(@Param("template_id") Integer templateId);

    Integer queryTemplateTotalNum(@Param("template_name") String templateName,
                                  @Param("source_system") String sourceSystem,
                                  @Param("create_username") String createUsername);

    List<String> querySourceSystem();

    List<String> queryEventName(String sourceSystem);

    Integer queryTemplateId(String sourceSystem, String eventName);

    Integer queryUsedTemplateTotalNum();

    Integer queryAuditTemplateTotalNum(Integer auditState);

    Integer queryAuditStatusById(Integer id);

    Boolean updateAuditStatusById(Integer id, Integer auditStatus);

    List<Integer> queryAuditStatusByIds(@Param("idsWithAuditStatusMap") Map<Integer, Integer> idsWithAuditStatusMap);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    Boolean updateAuditInfo(List<Integer> ids, String auditInfo);

    String queryAuditInfo(Integer id);
}
