package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.EntityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping("/new")
    public ResponseResult newTemplate(@RequestBody @Valid Template template) {

        Integer templateId = templateService.newTemplate(template);

        if (templateId > 0) {
            return ResponseResult.ok()
                    .setData("template_id", templateId)
                    .setData("success", true);
        }

        return ResponseResult.error()
                .setData("template_id", null)
                .setData("success", false);
    }

    @GetMapping("/query")
    public ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                         @RequestParam("page") @Min(1) Integer page,
                                         @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<Template> templates = templateService.queryTemplates(templateName, page, pageSize);

        return ResponseResult.ok()
                .setData("templates", templates);
    }

    @GetMapping("/{templateId}/query")
    public ResponseResult queryTemplate(@PathVariable Integer templateId) throws IllegalAccessException {
        return ResponseResult.ok()
                .setDataMap(EntityUtils.entityToMap(templateService.queryTemplate(templateId)));
    }

    @PostMapping("/{templateId}/update")
    public ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                         @RequestBody @Valid Template template) {
        Boolean success = templateService.updateTemplate(templateId, template);
        return ResponseResult.ok()
                .setData("template_id", templateId)
                .setData("success", success);
    }

    @DeleteMapping("/{templateId}/delete")
    public ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId) {
        return ResponseResult.ok()
                .setData("template_id", templateId)
                .setData("success", templateService.deleteTemplate(templateId));
    }
}
