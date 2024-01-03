package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deftac")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping("/new")
    public ResponseResult newTemplate(@RequestBody @Valid Template template) {
        Integer templateId;
        try {
            templateId = templateService.newTemplate(template);
        } catch (IllegalArgumentException e) {
            return new ResponseResult(400, "Illegal Argument in template_elements or default_op")
                    .setData("template_id", null)
                    .setData("success", false);
        }


        if (templateId > 0) {
            return ResponseResult.ok()
                    .setData("template_id", templateId)
                    .setData("success", true);
        }

        return ResponseResult.error()
                .setData("template_id", null)
                .setData("success", false);
    }
}
