package com.realtime.protection.server.rule.staticrule;


import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object) {
        log.info("新增静态规则: {}", object);
        //调用service新增
        staticRuleService.newStaticRuleObject(object);
        return ResponseResult.ok().setData("static_rule_name", object.getStaticRuleName());
    }

    //以Excel方式批量导入静态规则
    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), StaticRuleObject.class,
                new StaticRuleDataListener(staticRuleService)).sheet().doRead();
        return ResponseResult.ok();
    }

    //下载模板文件
    @GetMapping("/download")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("静态规则", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), StaticRuleObject.class)
                .sheet("静态规则")
                .doWrite(List.of());
    }


    /**
     * 删除静态规则（有的删了，有的没删，也返回false）
     */
    @DeleteMapping("/{ids}")
    public ResponseResult delete(@PathVariable List<Integer> ids) {
        log.info("根据id删除静态规则:{}", ids);
        //
        return ResponseResult.ok()
                .setData("static_rule_id", ids)
                .setData("success", staticRuleService.deleteStaticRules(ids));
    }

    /**
     * 修改静态规则
     */
    @PutMapping("/update")
    public ResponseResult updateStaticRule(@RequestBody @Valid StaticRuleObject object) {
        log.info("修改静态规则: {}", object);
        //调用service修改
        staticRuleService.updateStaticRule(object);
        return ResponseResult.ok();
    }

    /**
     * 根据id查询静态规则
     * 路径参数：通过请求URL直接传递参数，使用{…}来标识该路径参数，需要使用 @PathVariable 获取路径参数
     */
    @RequestMapping("/{id}/query")
    public ResponseResult queryStaticRuleById(@PathVariable Integer id) {
        log.info("根据id查询静态规则:{}", id);
        StaticRuleObject object = staticRuleService.queryStaticRuleById(id);
        return ResponseResult.ok().setData("static_rule", object);
    }

    /**
     * 分页查询静态规则
     */
    @RequestMapping("/query")
    public ResponseResult queryStaticRule(String static_rule_name, Integer static_rule_id,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("多查询静态规则: {},{},{},{}", static_rule_name, static_rule_id, page, pageSize);
        //调用service新增
        List<StaticRuleObject> pageResult = staticRuleService.queryStaticRule(static_rule_name, static_rule_id, page, pageSize);
        return ResponseResult.ok().setData("static_rule_list", pageResult);
    }
}
