package com.realtime.protection.server.rule.dynamicrule;

import com.realtime.protection.configuration.entity.rule.dynamicrule.DynamicRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dynamicrule")
@Slf4j
public class DynamicRuleController implements DynamicRuleControllerApi {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    // 新增   要关联防护对象！！！！
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
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        log.info("分页查询动态规则: {}:{}:{}:{}", dynamicRuleName, dynamicRuleId, page, pageSize);
        //调用service查询
        return ResponseResult.ok()
                .setData("success", true)
                .setData("dynamic_rule_list", dynamicRuleService.queryDynamicRuleObject(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator, page, pageSize))
                .setData("total_num",dynamicRuleService.queryDynamicRuleTotalNum(dynamicRuleName, dynamicRuleId,
                        protectObjectSourceSystem, creator));
    }

    //详情查看？？  就是按id查询吧


    //审核？不需要

}
