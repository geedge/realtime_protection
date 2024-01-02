package com.realtime.protection.server.rule.staticrule;


import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staticrule")
public class StaticRuleController {

    @Autowired
    private StaticRuleService staticRuleService;

    /**
     * 新增静态规则
     */
    @RequestMapping("/new")
    public ResponseResult newStaticRuleObject(@RequestBody StaticRuleObject object){
//        log.info("新增部门: {}" , dept);
        //调用service新增
        staticRuleService.newStaticRuleObject(object);
        return ResponseResult.ok();
    }

}
