package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/template")
public class TemplateController implements TemplateControllerApi {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
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

    @Override
    @GetMapping("/query")
    public ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                         @RequestParam(value = "source_system", required = false) String sourceSystem,
                                         @RequestParam(value = "create_user_name", required = false) String createUserName,
                                         @RequestParam("page") @Min(1) Integer page,
                                         @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<Template> templates = templateService.queryTemplates(
                templateName, sourceSystem, createUserName,
                page, pageSize
        );

        return ResponseResult.ok()
                .setData("templates", templates)
                .setData("total_num", templateService.queryTemplateTotalNum(templateName, sourceSystem, createUserName));
    }

    @Override
    @GetMapping("/{templateId}/query")
    public ResponseResult queryTemplate(@PathVariable Integer templateId) {
        Template template = templateService.queryTemplate(templateId);
        if (template == null) {
            return ResponseResult.invalid()
                    .setMessage("无效的策略模板ID，也许该模板不存在？");
        }
        return ResponseResult.ok().setData("template", template);
    }

    @Override
    @GetMapping("/query/level")
    public ResponseResult queryTemplateProtectLevels() {
        List<Template> templates = templateService.queryTemplatesShort();
        return ResponseResult.ok().setData("templates", templates);
    }

    @Override
    @PostMapping("/{templateId}/update")
    public ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                         @RequestBody @Valid Template template) {
        Boolean success = templateService.updateTemplate(templateId, template);
        return ResponseResult.ok()
                .setData("template_id", templateId)
                .setData("success", success);
    }

    @Override
    @DeleteMapping("/{templateId}/delete")
    public ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId) {
        return ResponseResult.ok()
                .setData("template_id", templateId)
                .setData("success", templateService.deleteTemplate(templateId));
    }

    @Override
    @GetMapping("/query/source_system")
    public ResponseResult querySourceSystem() {
        return ResponseResult.ok()
                .setData("source_system", templateService.querySourceSystem());
    }

    @Override
    @GetMapping("/query/event_name/{sourceSystem}")
    public ResponseResult queryEventName(@PathVariable String sourceSystem) {
        return ResponseResult.ok()
                .setData("event_name", templateService.queryEventName(sourceSystem));
    }

    @Override
    @PostMapping("/query/templateId")
    public ResponseResult queryTemplateId(@RequestBody Map<String,String> map) {
        String sourceSystem = map.get("source_system");
        String eventName = map.get("event_name");
        return ResponseResult.ok()
                .setData("template_id", templateService.queryTemplateId(sourceSystem, eventName));
    }

    /*
    策略模板数据统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult statisticsTemplate() {
        return ResponseResult.ok()
                .setData("template_num", templateService.queryTemplateTotalNum(null, null, null))
                .setData("template_used_num", templateService.queryUsedTemplateTotalNum())
                .setData("template_audit_num", templateService.queryAuditTemplateTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("template_unaudit_num", templateService.queryAuditTemplateTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())
                ));
    }


    /**
     * 审批
     */
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateTemplateAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("template_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .addDataMap(templateService.updateAuditStatus(id, auditStatus))
                .setData("template_id", id);
    }

    /**
     * 批量审批
     */
    /*
    @PostMapping("/auditbatch")
    public ResponseResult updateDynamicRuleAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap) {
        List<Integer> errorIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()){
            return new ResponseResult(400, "id or status is invalid")
                    .setData("staticRule_id", errorIds)
                    .setData("success", false);
        }

        return ResponseResult.ok();

//                .setData("success",dynamicRuleService.updateAuditStatusBatch(idsWithAuditStatusMap));
    }
*/
}
