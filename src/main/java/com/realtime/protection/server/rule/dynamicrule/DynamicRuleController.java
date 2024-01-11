package com.realtime.protection.server.rule.dynamicrule;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("dynamicrule")
@Slf4j
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    // 新增   要关联防护对象！！！！
    @RequestMapping("/new")
    public ResponseResult newDynamicRuleObject(@RequestBody @Valid DynamicRuleObject dynamicRuleObject) {
        log.info("新增动态规则: {}", dynamicRuleObject);
        //调用service新增
        Integer dynamicRuleObjectId = dynamicRuleService.newDynamicRuleObject(dynamicRuleObject);
        return ResponseResult.ok().
                setData("dynamic_rule_id", dynamicRuleObjectId)
                .setData("dynamic_rule_name", dynamicRuleObject.getDynamicRuleName())
                .setData("success", true);
    }

    //以excel文件方式批量导入,但动态规则没有这个需求
    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), DynamicRuleObject.class,
                new DynamicRuleDataListener(dynamicRuleService)).sheet().doRead();
        return ResponseResult.ok();
    }


    //id删除
    @RequestMapping("/{dynamicRuleId}/delete")
    public ResponseResult deleteDynamicRuleObject(@PathVariable Integer dynamicRuleId) {
        log.info("删除动态规则: {}", dynamicRuleId);
        //调用service删除
        dynamicRuleService.deleteDynamicRuleObject(dynamicRuleId);

        return ResponseResult.ok();
    }

    //批量删除
    @RequestMapping("/delete")
    public ResponseResult deleteDynamicRuleObjects(@RequestBody List<Integer> dynamicRuleIds) {
        log.info("批量删除动态规则: {}", dynamicRuleIds);
        //调用service删除

        return ResponseResult.ok()
                .setData("success", dynamicRuleService.deleteDynamicRuleObjects(dynamicRuleIds));
    }

    //修改
    @RequestMapping("/{dynamicRuleId}/update")
    public ResponseResult updateDynamicRuleObject(@PathVariable Integer dynamicRuleId,
                                                  @RequestBody @Valid DynamicRuleObject dynamicRuleObject) {
        log.info("修改动态规则: {}:{}",
                dynamicRuleId, dynamicRuleObject.getDynamicRuleName());

        dynamicRuleService.updateDynamicRuleObject(dynamicRuleId, dynamicRuleObject);

        return ResponseResult.ok()
                .setData("dynamic_rule_id", dynamicRuleId)
                .setData("success", true);
    }

    //id查询
    @RequestMapping("/{dynamicRuleId}/query")
    public ResponseResult queryDynamicRuleObjectById(@PathVariable Integer dynamicRuleId) {
        log.info("查询动态规则: {}", dynamicRuleId);
        //调用service查询
        return ResponseResult.ok()
                .setData("dynamic_rule", dynamicRuleService.queryDynamicRuleById(dynamicRuleId));
    }

    //分页查询
    @RequestMapping("/query")
    public ResponseResult queryDynamicRuleObject(@RequestParam(value = "dynamic_rule_name", required = false) String dynamicRuleName,
                                                 @RequestParam(value = "dynamic_rule_id", required = false) Integer dynamicRuleId,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        log.info("分页查询动态规则: {}:{}:{}:{}", dynamicRuleName, dynamicRuleId, page, pageSize);
        //调用service查询
        return ResponseResult.ok()
                .setData("dynamic_rule_list", dynamicRuleService.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId, page, pageSize));
    }

    //详情查看？？  查看什么
}
