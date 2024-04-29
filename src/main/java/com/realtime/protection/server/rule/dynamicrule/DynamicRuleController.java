package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public ResponseResult newDynamicRuleObject(@RequestBody @Valid DynamicRuleObject dynamicRuleObject) {
        log.info("新增动态规则: {}", dynamicRuleObject);
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
        log.info("删除动态规则: {}", id);
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
                                                 @RequestParam(value = "id", required = false) Integer dynamicRuleId,
                                                 @RequestParam(value = "source_system", required = false) String protectObjectSourceSystem,
                                                 @RequestParam(value = "creator", required = false) String creator,
                                                 @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        log.info("分页查询动态规则: {}:{}:{}:{}", dynamicRuleName, dynamicRuleId, page, pageSize);
        //调用service查询
        return ResponseResult.ok()
                .setData("success", true)
                .setData("dynamic_rule_list", dynamicRuleService.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator, auditStatus, page, pageSize))
                .setData("total_num",dynamicRuleService.queryDynamicRuleTotalNum(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator, auditStatus));
    }

    //详情查看？？  就是按id查询吧


    /**
     * 审批
     */
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateDynamicRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("dynamicRule_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .addDataMap(dynamicRuleService.updateAuditStatus(id, auditStatus))
                .setData("dynamicRule_id", id);
    }

    /**
     * 批量审批
     */
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

        return ResponseResult.ok()
                .setData("success",                                                                                dynamicRuleService.updateAuditStatusBatch(idsWithAuditStatusMap));
    }


    /**
     * 获取统计数据
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult getStaticRuleStatisticsData(){

        return ResponseResult.ok()
                .setData("dynamic_rule_num", dynamicRuleService.queryDynamicRuleTotalNum(null,null, null,
                        null, null))
                .setData("dynamic_rule_used_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.USING.getState())
                ))
                .setData("dynamic_rule_audit_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("dynamic_rule_unaudit_num", dynamicRuleService.queryAuditDynamicRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())
                ));
    }


}
