package com.realtime.protection.server.rule.staticrule;


import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    public ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object,
                                              HttpServletRequest request) {
//        log.info("新增静态规则: {}", object);
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            object.setStaticRuleCreateUsername(user.name);
            object.setStaticRuleCreateUserId(Integer.valueOf(user.uid));
            object.setStaticRuleCreateDepart(user.getOrgName());
        }
        //调用service新增
        staticRuleService.newStaticRuleObject(object);
        return ResponseResult.ok()
                .setData("static_rule_id", object.getStaticRuleId())
                .setData("static_rule_name", object.getStaticRuleName());
    }

    //以Excel方式批量导入静态规则
    @PostMapping("/upload")
    @Override
    public ResponseResult uploadFile(@NotNull(message = "uploadFile字段不能为空")
                                         MultipartFile uploadFile) throws IOException {
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
                                          @RequestParam(name = "sip", required = false) String static_rule_sip,
                                          @RequestParam(name = "sport", required = false) Integer static_rule_sport,
                                          @RequestParam(name = "dip", required = false) String static_rule_dip,
                                          @RequestParam(name = "dport", required = false) Integer static_rule_dport,
                                          @RequestParam(name = "auditStatus", required = false) Integer auditStatus,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("多查询静态规则: {},{},{},{}", static_rule_name, static_rule_id, page, pageSize);
        //调用service新增
        List<StaticRuleObject> pageResult = staticRuleService.queryStaticRule(
                static_rule_name, static_rule_id,
                static_rule_creator, static_rule_sip, static_rule_sport, static_rule_dip, static_rule_dport,  auditStatus, page, pageSize);
        return ResponseResult.ok()
                .setData("static_rule_list", pageResult)
                .setData("total_num",staticRuleService.queryStaticRuleTotalNum(static_rule_name, static_rule_id,
                        static_rule_creator, static_rule_sip, static_rule_sport, static_rule_dip, static_rule_dport, auditStatus));
    }

    @Override
    @GetMapping("/query/{ids}")
    public ResponseResult queryStaticRuleByIds(@PathVariable List<Integer> ids) {
        List<StaticRuleObject> staticRuleObjects = staticRuleService.queryStaticRuleByIds(ids);
        //调用service查询
        return ResponseResult.ok()
                .setData("success", true)
                .setData("static_rule", staticRuleObjects);
    }

    /**
     * 修改审核状态
     */
    @Override
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateStaticRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("staticRule_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .addDataMap(staticRuleService.updateAuditStatus(id, auditStatus))
                .setData("staticRule_id", id);

    }


    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateStaticRuleAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap) {
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
                .setData("success", staticRuleService.updateAuditStatusBatch(idsWithAuditStatusMap));
    }

    /**
     * 获取统计数据
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult getStaticRuleStatisticsData(){

        return ResponseResult.ok()
                .setData("static_rule_num", staticRuleService.queryStaticRuleTotalNum(null, null, null, null, null, null, null, null))
                .setData("static_rule_used_num", staticRuleService.queryAuditStaticRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.USING.getState())
                ))
                .setData("static_rule_audit_num", staticRuleService.queryAuditStaticRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("static_rule_unaudit_num", staticRuleService.queryAuditStaticRuleTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())));
    }


    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", staticRuleService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", staticRuleService.queryAuditInfo(id));
    }

}
