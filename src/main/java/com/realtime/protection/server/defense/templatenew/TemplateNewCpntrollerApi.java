package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface TemplateNewCpntrollerApi {


    @GetMapping("/query")
    ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                  @RequestParam(value = "source_system", required = false) String sourceSystem,
                                  @RequestParam(value = "create_user_name", required = false) String createUserName,
                                  @RequestParam(value = "template_id", required = false) Integer templateId,
                                  @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                  @RequestParam(value = "event_type", required = false) String eventType,
                                  @RequestParam(value = "protect_level", required = false) Integer protectLevel,

                                  @RequestParam("page") @Min(1) Integer page,
                                  @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{templateId}/query")
    ResponseResult queryTemplate(@PathVariable Integer templateId);



    @PostMapping("/{templateId}/update")
    ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                  @RequestBody @Valid TemplateNew template);

    @DeleteMapping("/{templateId}/delete")
    ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId);

    @GetMapping("/query/source_system")
    ResponseResult querySourceSystem();

    @GetMapping("/query/event_name/{sourceSystem}")
    ResponseResult queryEventName(@PathVariable String sourceSystem);

    /*
        策略模板数据统计
         */
    @GetMapping("/statistics")
    ResponseResult statisticsTemplate();

    @GetMapping("/{id}/audit/{auditStatus}")
    ResponseResult updateTemplateAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus);

    @PostMapping("/auditbatch")
    ResponseResult updateWhiteListAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap);

    /*
        修改审批信息
         */
    @PostMapping("/auditInfo/{ids}")
    ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                   @RequestBody Map<String, String> auditInfo);

    @GetMapping("/auditInfo/{id}")
    ResponseResult queryAuditInfo(@PathVariable Integer id);
}
