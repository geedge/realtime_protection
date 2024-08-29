package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/template/")
public class TemplateController implements TemplateNewCpntrollerApi{
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @Override
    @PostMapping("/new")
    public ResponseResult newTemplate(@RequestBody @Valid TemplateNew template,
                                      @Autowired HttpServletRequest request) {
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            template.setCreateUsername(user.name);
            template.setCreateUserId(Integer.valueOf(user.uid));
            template.setCreateDepart(user.getOrgName());
        }
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

    /*
    删 改 查 审核。。。
     */

    @Override
    @GetMapping("/query")
    public ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                         @RequestParam(value = "source_system", required = false) String sourceSystem,
                                         @RequestParam(value = "create_user_name", required = false) String createUserName,
                                         @RequestParam(value = "template_id", required = false) String templateId,
                                         @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                         @RequestParam(value = "event_type", required = false) String eventType,
                                         @RequestParam(value = "protect_level", required = false) Integer protectLevel,

                                         @RequestParam("page") @Min(1) Integer page,
                                         @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<TemplateNew> templates = templateService.queryTemplates(
                templateName, sourceSystem, createUserName, templateId,
                auditStatus,eventType,protectLevel,
                page, pageSize
        );

        return ResponseResult.ok()
                .setData("templates", templates)
                .setData("total_num", templateService.queryTemplateTotalNum(
                        templateName, sourceSystem, createUserName, templateId,
                        auditStatus, eventType, protectLevel));
    }

    @Override
    @GetMapping("/{templateId}/query")
    public ResponseResult queryTemplate(@PathVariable Integer templateId) {
        TemplateNew template = templateService.queryTemplate(templateId);
        if (template == null) {
            return ResponseResult.invalid()
                    .setMessage("无效的策略模板ID，也许该模板不存在？");
        }
        return ResponseResult.ok().setData("template", template);
    }

//    @Override
//    @GetMapping("/query/level")
//    public ResponseResult queryTemplateProtectLevels() {
//        List<Template> templates = templateService.queryTemplatesShort();
//        return ResponseResult.ok().setData("templates", templates);
//    }

    @Override
    @PostMapping("/{templateId}/update")
    public ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                         @RequestBody @Valid TemplateNew template) {
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



//    @Override
//    @GetMapping("/query/event_name/{sourceSystem}")
//    public ResponseResult queryEventName(@PathVariable String sourceSystem) {
//        return ResponseResult.ok()
//                .setData("event_name", templateService.queryEventName(sourceSystem));
//    }

//    @Override
//    @PostMapping("/query/templateId")
//    public ResponseResult queryTemplateId(@RequestBody Map<String,String> map) {
//        String sourceSystem = map.get("source_system");
//        String eventName = map.get("event_name");
//        return ResponseResult.ok()
//                .setData("template_id", templateService.queryTemplateId(sourceSystem, eventName));
//    }

    /*
    策略模板数据统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult statisticsTemplate() {
        return ResponseResult.ok()
                .setData("template_num", templateService.queryTemplateTotalNum(null, null, null, null, null, null, null))
                .setData("template_used_num", templateService.queryUsedTemplateTotalNum())
                .setData("template_audit_num", templateService.queryAuditTemplateTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("template_unaudit_num", templateService.queryAuditTemplateTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())
                ))
                .setData("template_returned_num", templateService.queryAuditTemplateTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.RETURNED.getState())
                ));
    }


    /**
     * 审批
     */
    @Override
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateTemplateAuditStatus(@PathVariable Integer id,
                                                    @PathVariable Integer auditStatus,
                                                    @Autowired HttpServletRequest request) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("template_id", id)
                    .setData("success", false);
        }
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        String auditUserName = null;
        Integer auditUserId = null;
        String auditUserDepart = null;
        if (user != null) {
            auditUserName= user.name;
            auditUserId = Integer.valueOf(user.uid);
            auditUserDepart = user.getOrgName();
        }
        return ResponseResult.ok()
                .addDataMap(templateService.updateAuditStatus(id, auditStatus,
                        auditUserName, auditUserId, auditUserDepart))
                .setData("template_id", id);
    }


    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateWhiteListAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap,
                                                          @Autowired HttpServletRequest request) {
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
                    .setData("id", errorIds)
                    .setData("success", false);
        }
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        String auditUserName = null;
        Integer auditUserId = null;
        String auditUserDepart = null;
        if (user != null) {
            auditUserName= user.name;
            auditUserId = Integer.valueOf(user.uid);
            auditUserDepart = user.getOrgName();
        }
        return ResponseResult.ok()
                .setData("success", templateService.updateAuditStatusBatch(idsWithAuditStatusMap,
                        auditUserName, auditUserId, auditUserDepart));
    }

    /*
    修改审批信息
     */
    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", templateService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", templateService.queryAuditInfo(id));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseResult queryHistory(@PathVariable Integer id,
                                       @RequestParam(value = "page", required = true) Integer page,
                                       @RequestParam(value = "page_size", required = true) Integer pageSize) {

        return ResponseResult.ok()
                .setData("history", templateService.queryHistory(id, page, pageSize));
    }
}
