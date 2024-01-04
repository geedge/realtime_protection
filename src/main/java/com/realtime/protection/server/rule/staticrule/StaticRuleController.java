package com.realtime.protection.server.rule.staticrule;


import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@RestController
@RequestMapping("/staticrule")
@Slf4j
public class StaticRuleController {

    private final StaticRuleService staticRuleService;
    public StaticRuleController(StaticRuleService staticRuleService) {
        this.staticRuleService = staticRuleService;
    }

    /**
     * 新增静态规则
     */
    @PostMapping("/new")
    public ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object){
        log.info("新增静态规则: {}" , object);
        //调用service新增
        staticRuleService.newStaticRuleObject(object);
        return ResponseResult.ok().setData("static_rule_name",object.getStaticRuleName());
    }

    /**
     * 删除静态规则
     */
    @DeleteMapping("/{ids}")
    public ResponseResult delete(@PathVariable List<Integer> ids){
        log.info("根据id删除静态规则:{}",ids);
        //调用service删除
        staticRuleService.deleteStaticRule(ids);
        return ResponseResult.ok();
    }

    /**
     * 修改静态规则
     */
    @PutMapping("/update")
    public ResponseResult updateStaticRule(@RequestBody @Valid StaticRuleObject object){
        log.info("修改静态规则: {}" , object);
        //调用service修改
        staticRuleService.updateStaticRule(object);
        return ResponseResult.ok();
    }

    /**
     * 根据id查询静态规则
     * 路径参数：通过请求URL直接传递参数，使用{…}来标识该路径参数，需要使用 @PathVariable 获取路径参数
     */
    @RequestMapping("/{id}/query")
        public ResponseResult queryStaticRuleById(@PathVariable Integer id){
            log.info("根据id查询静态规则:{}",id);
            StaticRuleObject object = staticRuleService.queryStaticRuleById(id);
            return ResponseResult.ok().setData("static_rule",object);
        }

    /**
     * 分页查询静态规则
     */
    @RequestMapping("/query")
    public ResponseResult queryStaticRule(String static_rule_name, Integer static_rule_id,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize
    ){
        log.info("多查询静态规则: {},{},{},{}", static_rule_name,static_rule_id,page,pageSize);
        //调用service新增
        List<StaticRuleObject> pageResult = staticRuleService.queryStaticRule(static_rule_name,static_rule_id,page,pageSize);
        return ResponseResult.ok().setData("static_rule_list",pageResult);
    }
}
