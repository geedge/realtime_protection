package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface TemplateMapper {
    void newTemplate(@Param("template")TemplateNew template);

    List<TemplateNew> queryTemplates(@Param("template_name") String templateName,
                                  @Param("source_system") String sourceSystem,
                                  @Param("create_username") String createUsername,
                                  @Param("template_id")Integer templateId,
                                  @Param("audit_status")Integer auditStatus,
                                     @Param("event_type")String eventType,
                                     @Param("protect_level")Integer protectLevel,
                                  @Param("page") Integer page,
                                  @Param("page_size") Integer pageSize);


    Boolean updateTemplateInformation(@Param("template") TemplateNew template);

    TemplateNew queryTemplate(@Param("template_id") Integer templateId);

    @Delete("DELETE FROM t_strategy_template_new WHERE strategy_template_id = #{template_id}")
    Boolean deleteTemplate(@Param("template_id") Integer templateId);

    Integer queryTemplateTotalNum(@Param("template_name") String templateName,
                                  @Param("source_system") String sourceSystem,
                                  @Param("create_username") String createUsername,
                                  @Param("template_id")Integer templateId,
                                  @Param("audit_status")Integer auditStatus,
                                  @Param("event_type")String eventType,
                                  @Param("protect_level")Integer protectLevel
                                  );

    List<String> querySourceSystem();

//    List<String> queryEventName(String sourceSystem);

//    Integer queryTemplateId(String sourceSystem, String eventName);

    Integer queryUsedTemplateTotalNum();

    Integer queryAuditTemplateTotalNum(Integer auditState);

    Integer queryAuditStatusById(Integer id);

    Boolean updateAuditStatusById(Integer id, Integer auditStatus);

    List<Integer> queryAuditStatusByIds(@Param("idsWithAuditStatusMap") Map<Integer, Integer> idsWithAuditStatusMap);

    void updateAuditStatusByIdBatch(@Param("idWithAuditStatusBatch") Map<Integer, Integer> idWithAuditStatusBatch);

    Boolean updateAuditInfo(@Param("ids")List<Integer> ids,@Param("auditInfo") String auditInfo);

    String queryAuditInfo(Integer id);

    void updateStatusLogExpireTime(Integer id);

    void insertStatusLog(Integer id);

    void updateStatusLogExpireTimeBatch(List<Integer> ids);

    void insertStatusLogBatch(List<Integer> ids);

    List<TemplateNew> queryHistory(Integer id, Integer page, Integer pageSize);
}
