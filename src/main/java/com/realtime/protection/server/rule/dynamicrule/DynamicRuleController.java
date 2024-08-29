package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("dynamicrule")
@Slf4j
public class DynamicRuleController implements DynamicRuleControllerApi {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    //
    @Override
    @PostMapping("/new")
    public ResponseResult newDynamicRuleObject(@RequestBody @Valid DynamicRuleObject dynamicRuleObject,
                                               @Autowired HttpServletRequest request) {
//        log.info("新增动态规则: {}", dynamicRuleObject);
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            dynamicRuleObject.setDynamicRuleCreateUsername(user.name);
            dynamicRuleObject.setDynamicRuleCreateUserId(Integer.valueOf(user.uid));
            dynamicRuleObject.setDynamicRuleCreateDepart(user.getOrgName());
        }

        //调用service新增
        Integer dynamicRuleObjectId = dynamicRuleService.newDynamicRuleObject(dynamicRuleObject);
        return ResponseResult.ok().
                setData("dynamic_rule_id", dynamicRuleObjectId)
                .setData("dynamic_rule_name", dynamicRuleObject.getDynamicRuleName())
                .setData("success", true);
    }

    /*
    //以excel文件方式批量导入,但动态规则没有这个需求
    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), DynamicRuleObject.class,
                new DynamicRuleDataListener(dynamicRuleService)).sheet().doRead();
        return ResponseResult.ok();
    }
    */

    //id删除
    @Override
    @DeleteMapping("/{id}/delete")
    public ResponseResult deleteDynamicRuleObject(@PathVariable Integer id) {
//        log.info("删除动态规则: {}", id);
        //调用service删除
        dynamicRuleService.deleteDynamicRuleObject(id);

        return ResponseResult.ok();
    }

    //批量删除
    @Override
    @DeleteMapping("/{ids}")
    public ResponseResult deleteDynamicRuleObjects(@PathVariable List<Integer> ids) {
        //log.info("批量删除动态规则: {}", ids);
        //调用service删除

        return ResponseResult.ok()
                .setData("success", dynamicRuleService.deleteDynamicRuleObjects(ids));
    }

    //修改
    @Override
    @PostMapping("/{id}/update")
    public ResponseResult updateDynamicRuleObject(@PathVariable Integer id,
                                                  @RequestBody @Valid DynamicRuleObject dynamicRuleObject) {
        log.info("修改动态规则: {}:{}",
                id, dynamicRuleObject.getDynamicRuleName());

        return ResponseResult.ok()
                .setData("dynamic_rule_id", id)
                .setData("success", dynamicRuleService.updateDynamicRuleObject(id, dynamicRuleObject));
    }

    //id查询
    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryDynamicRuleObjectById(@PathVariable Integer id) {
        DynamicRuleObject dynamicRuleObject = dynamicRuleService.queryDynamicRuleById(id);
        //调用service查询
        return ResponseResult.ok()
                .setData("success", true)
                .setData("dynamic_rule", dynamicRuleObject);
    }

    @Override
    @GetMapping("/query/{ids}")
    public ResponseResult queryDynamicRuleObjectByIds(@PathVariable List<Integer> ids) {
        List<DynamicRuleObject> dynamicRuleObjects = dynamicRuleService.queryDynamicRuleByIds(ids);
        //调用service查询
        return ResponseResult.ok()
                .setData("success", true)
                .setData("dynamic_rule", dynamicRuleObjects);
    }

    //分页查询
    @Override
    @GetMapping("/query")
    public ResponseResult queryDynamicRuleObject(@RequestParam(value = "name", required = false) String dynamicRuleName,
                                                 @RequestParam(value = "id", required = false) String dynamicRuleId,
                                                 @RequestParam(value = "source_system", required = false) String protectObjectSourceSystem,
                                                 @RequestParam(value = "creator", required = false) String creator,
                                                 @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                                 @RequestParam(value = "event_type", required = false) String eventType,
                                                 @RequestParam(value = "protect_level", required = false) Integer protectLevel,
                                                 @RequestParam(value = "template_name", required = false) String templateName,
                                                 @RequestParam(name = "auditUserName", required = false) String auditUserName,
                                                 @RequestParam(name = "createTime", required = false) LocalDate createTime,

                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
//        log.info("分页查询动态规则: {}:{}:{}:{}", dynamicRuleName, dynamicRuleId, page, pageSize);
        //调用service查询
        String createDateStr = null;
        if (createTime != null) {
            createDateStr = createTime.toString();
        }
        return ResponseResult.ok()
                .setData("success", true)
                .setData("dynamic_rule_list", dynamicRuleService.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator, auditStatus, eventType, protectLevel, templateName,
                        auditUserName, createDateStr, page, pageSize))
                .setData("total_num", dynamicRuleService.queryDynamicRuleTotalNum(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator, auditStatus, eventType,
                        protectLevel, templateName, auditUserName, createDateStr));
    }

    //详情查看？？  就是按id查询吧


    /**
     * 审批
     */
    @Override
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateDynamicRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus,
                                                       @Autowired HttpServletRequest request) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("dynamicRule_id", id)
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
                .addDataMap(dynamicRuleService.updateAuditStatus(id, auditStatus
                ,auditUserName, auditUserId, auditUserDepart))
                .setData("dynamicRule_id", id);
    }

    /**
     * 批量审批
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateDynamicRuleAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap,
                                                            @Autowired HttpServletRequest request) {
        List<Integer> errorIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("staticRule_id", errorIds)
                    .setData("success", false);
        }
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        String auditUserName = null;
        String auditUserId = null;
        String auditUserDepart = null;
        if (user != null) {
            auditUserName= user.name;
            auditUserId = user.uid;
            auditUserDepart = user.getOrgName();
        }

        return ResponseResult.ok()
                .setData("success", dynamicRuleService.updateAuditStatusBatch(idsWithAuditStatusMap,
                         auditUserName,  auditUserId,  auditUserDepart));
    }


    /**
     * 获取统计数据
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult getStaticRuleStatisticsData() {

        return ResponseResult.ok()
                .setData("dynamic_rule_num", dynamicRuleService.queryDynamicRuleTotalNum(null, null, null,
                        null, null, null,
                        null, null,null,null))
                .setData("dynamic_rule_used_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.USING.getState())
                ))
                .setData("dynamic_rule_audit_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("dynamic_rule_unaudit_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())
                ))
                .setData("dynamic_rule_returned_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.RETURNED.getState())
                ));
    }

    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", dynamicRuleService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", dynamicRuleService.queryAuditInfo(id));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseResult queryHistory(@PathVariable Integer id,
                                       @RequestParam(value = "page", required = true) Integer page,
                                       @RequestParam(value = "page_size", required = true) Integer pageSize) {

        return ResponseResult.ok()
                .setData("history", dynamicRuleService.queryHistory(id, page, pageSize));
    }

}
