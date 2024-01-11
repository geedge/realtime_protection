package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.defense.template.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {
    void newTemplate(@Param("template") Template template);

    void newProtectLevel(@Param("level") ProtectLevel protectLevel);

    List<Template> queryTemplates(@Param("template_name") String templateName,
                                  @Param("page") Integer page,
                                  @Param("page_size") Integer pageSize);

    ProtectLevel queryProtectLevel(@Param("level_id") Integer protectLevelId);

    Boolean updateTemplateInformation(@Param("template") Template template);

    Template queryTemplate(@Param("template_id") Integer templateId);

    Boolean deleteTemplate(@Param("template_id") Integer templateId);
}
