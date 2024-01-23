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
public class StaticRuleController implements StaticRuleControllerApi {

    private final StaticRuleService staticRuleService;

    public StaticRuleController(StaticRuleService staticRuleService) {
        this.staticRuleService = staticRuleService;
    }

    /**
     * 新增静态规则
     */
    @PostMapping("/new")
    @Override
    public ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object) {
        log.info("新增静态规则: {}", object);
        //调用service新增
        staticRuleService.newStaticRuleObject(object);
        return ResponseResult.ok()
                .setData("static_rule_id", object.getStaticRuleId())
                .setData("static_rule_name", object.getStaticRuleName());
    }

    //以Excel方式批量导入静态规则
    @PostMapping("/upload")
    @Override
    public ResponseResult uploadFile(@RequestPart("file")MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), StaticRuleObject.class,
                new StaticRuleDataListener(staticRuleService)).sheet().doRead();
        return ResponseResult.ok();
    }

    //下载模板文件
    @Override
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
     * 按id删除静态规则
     */
    @Override
    @DeleteMapping("/{id}/delete")
    public ResponseResult deleteStaticRule(@PathVariable Integer id) {
        log.info("根据id删除静态规则:{}", id);
        //调用service删除
        staticRuleService.deleteStaticRuleById(id);
        return ResponseResult.ok();
    }

    /**
     * 删除静态规则（有的删了，有的没删，也返回false）
     */
    @Override
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
    @Override
    @PostMapping("/{id}/update")
    public ResponseResult updateStaticRule(@PathVariable Integer id,
                                           @RequestBody @Valid StaticRuleObject object) {
        log.info("修改静态规则: {}", object);
        //调用service修改
        Boolean updateValid = staticRuleService.updateStaticRule(id, object);
        if (!updateValid) {
            return ResponseResult.invalid().setMessage("id is invalid");
        }
        return ResponseResult.ok().setData("success",updateValid);
    }

    /**
     * 根据id查询静态规则
     * 路径参数：通过请求URL直接传递参数，使用{…}来标识该路径参数，需要使用 @PathVariable 获取路径参数
     */
    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryStaticRuleById(@PathVariable Integer id) {
        log.info("根据id查询静态规则:{}", id);
        StaticRuleObject object = staticRuleService.queryStaticRuleById(id);
        if (object == null) {
            return ResponseResult.invalid().setMessage("id is invalid");
        }
        return ResponseResult.ok().setData("static_rule", object);
    }

    /**
     * 分页查询静态规则
     */
    @Override
    @GetMapping("/query")
    public ResponseResult queryStaticRule(@RequestParam(name = "name", required = false) String static_rule_name,
                                          @RequestParam(name = "id", required = false) Integer static_rule_id,
                                          @RequestParam(name = "creator", required = false) String static_rule_creator,
                                          @RequestParam(name = "ip", required = false) String static_rule_ip,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("多查询静态规则: {},{},{},{}", static_rule_name, static_rule_id, page, pageSize);
        //调用service新增
        List<StaticRuleObject> pageResult = staticRuleService.queryStaticRule(
                static_rule_name, static_rule_id,
                static_rule_creator, static_rule_ip, page, pageSize);
        return ResponseResult.ok()
                .setData("static_rule_list", pageResult)
                .setData("total_num",staticRuleService.queryStaticRuleTotalNum(static_rule_name, static_rule_id,
                        static_rule_creator, static_rule_ip));
    }

    /**
     * 修改审核状态
     */
    @Override
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateStaticRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .addDataMap(staticRuleService.updateAuditStatus(id, auditStatus))
                .setData("staticRule_id", id);

    }


}
