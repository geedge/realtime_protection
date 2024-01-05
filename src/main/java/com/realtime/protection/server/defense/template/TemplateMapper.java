package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {
    void newTemplate(@Param("template") Template template);

    List<Template> queryTemplates(@Param("template_name") String templateName,
                                  @Param("page") Integer page,
                                  @Param("page_size") Integer pageSize);

    Boolean updateTemplateInformation(@Param("template") Template template);

    void countTemplateRunningTasks(@Param("template_id") Integer templateId);

    Boolean addTemplateUsedTimes(@Param("template_id") Integer templateId, @Param("add_times") Integer addTimes);
}
