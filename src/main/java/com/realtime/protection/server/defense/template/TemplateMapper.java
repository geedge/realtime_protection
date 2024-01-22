package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.defense.template.Template;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @Delete("DELETE FROM t_strategy_template WHERE strategy_template_id = #{template_id}")
    Boolean deleteTemplate(@Param("template_id") Integer templateId);

    @Select("SELECT COUNT(strategy_template_id) FROM t_strategy_template")
    Integer queryTemplateTotalNum();
}
