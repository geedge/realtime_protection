package com.realtime.protection.server.whitelist;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/whiteobj")
public class WhiteListController implements WhiteListControllerApi {

    private final WhiteListService whiteListService;

    public WhiteListController(WhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @Override
    @PostMapping("/new")
    public ResponseResult newWhitelistObject(@RequestBody @Valid WhiteListObject object,
                                             @Autowired HttpServletRequest request) {

        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            object.setCreateUserName(user.name);
            object.setCreateUserId(user.uid);
            object.setCreateUserDepartment(user.getOrgName());
        }

        Integer whiteListObjectId = whiteListService.newWhiteListObject(object);

        return ResponseResult.ok()
                .setData("whiteobj_name", object.getWhiteListName())
                .setData("whiteobj_id", whiteListObjectId)
                .setData("success", true);
    }

    //以excel文件方式批量导入、
    //post
    @Override
    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile,
                                     @Autowired HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        EasyExcel.read(uploadFile.getInputStream(), WhiteListObject.class,
                new WhiteListDataListener(whiteListService, user)).sheet().doRead();
        return ResponseResult.ok();
    }

    //下载模板文件
    @Override
    @GetMapping("/download")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("白名单", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), WhiteListObject.class)
                .sheet("白名单")
                .doWrite(List.of());
    }

    @Override
    @GetMapping("/query")
    public ResponseResult queryWhiteListObject(@RequestParam(value = "whiteobj_name", required = false) String whiteListName,
                                               @RequestParam(value = "whiteobj_id", required = false) Integer whiteListId,
                                               @RequestParam(value = "system_name", required = false) String systemName,
                                               @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                               @RequestParam(value = "creator", required = false) String creator,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return new ResponseResult(400, "page or page_size is invalid")
                    .setData("whiteobj_list", null);
        }
        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListService.queryWhiteListObject(whiteListName, whiteListId, systemName, auditStatus, creator, page, pageSize))
                .setData("total_num", whiteListService.queryWhiteListTotalNum(whiteListName, whiteListId, systemName, auditStatus, creator));
    }

    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryWhiteListObjectById(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_list", null);
        }

        WhiteListObject whiteListObject = whiteListService.queryWhiteListObjectById(id);
        if (whiteListObject == null) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_list", null);
        }
        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListObject);
    }

    @Override
    //根据id删除
    @DeleteMapping("/{id}/delete")
    public ResponseResult deleteWhiteListObjectById(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        Integer num = whiteListService.deleteWhiteListObjectById(id);
        if (num == 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .setData("whiteobj_id", id)
                .setData("success", true);
        //还要return 白名单名称吗,还要在查表看他的名称
    }

    @Override
    @DeleteMapping("/{ids}")
    public ResponseResult deleteWhiteListObjects(@PathVariable List<Integer> ids) {
        //Post不支持body为json。。。 body只能是[9,10]

        return ResponseResult.ok()
                .setData("whiteobj_list", ids)
                .setData("success", whiteListService.deleteWhiteListObjects(ids));
    }

    @Override
    @PostMapping("/{id}/update")
    public ResponseResult updateWhiteListObject(@PathVariable Integer id,
                                                @RequestBody WhiteListObject object) {
        object.setWhiteListId(id);
        Integer num = whiteListService.updateWhiteListObject(id, object);
        if (num == 0) {
            return ResponseResult.invalid("id is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .setData("whiteobj_id", id)
                .setData("success", true);
    }

/*
    //查询ip是否存在于白名单
    @RequestMapping("/exist")
    public ResponseResult existWhiteListObject(@RequestParam(value = "ruleId", required = true) Integer ruleId,
                                               @RequestParam(value = "ruleType", required = true) Integer ruleType) {
        //是请求规则的id，然后判断这个id所属的ip是否在白名单中吗
        //静态应该可以，但动态的，动态是实时过来告警信息，不存储规则？ 存的话也行，那这里要区分id是静态的还是动态的
        //这里先走通静态的，要获取规则的源IP和目的IP，去白名单select看有没有(有的还有IP掩码,暂未实现)

        //返回在白名单中的IP，
        List<String> ruleInWhiteListIds = whiteListService.existWhiteListObject(ruleId, ruleType);

        return ResponseResult.ok()
                .setData("ip_list", ruleInWhiteListIds);
    }
*/

    @Override
    //修改审核状态
    @GetMapping("/{id}/audit/{auditStatus}")
    public ResponseResult updateWhiteListObjectAuditStatus(@PathVariable Integer id,
                                                           @PathVariable Integer auditStatus,
                                                           @Autowired HttpServletRequest request) {
        if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
            return new ResponseResult(400, "id或状态无效")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
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
                .addDataMap(whiteListService.updateWhiteListObjectAuditStatus(id, auditStatus,
                        auditUserName, auditUserId, auditUserDepart))
                .setData("whiteobj_id", id);

    }

    /*
    白名单审核思路：
    （1）按现在界面：任务创建时提示白名单命中。
         ①静态任务：任务还没通过审核进入RUNNING状态，指令也没有生成，目前只能check静态规则在不在白名单
         ②动态任务：任务还没通过审核进入RUNNING状态，告警信息没有接收、指令也没有生成，check不了白名单
    （2）若对指令进行白名单检查：（直接剔除指令中存在的白名单对象）
        存在的问题：指令如果是含有掩码的子网IP，就需要把子网拆分成一个个IP，从中剔除白名单IP，然后把一个个IP变成指令。
                 也就是把含有掩码的指令拆分成不包含掩码的多个指令，此时指令展示会很多。
        ①静态任务：指令生成后，自动剔除，若出现掩码，存在以上问题
        ②实时任务：告警接收后，经策略模板指令生成后，自动剔除白名单对象，若出现掩码，存在以上问题
        ③研判后认务：告警接收后，经策略模板指令生成后，自动剔除白名单对象，若出现掩码，包含掩码的指令都拆分多个指令了，
                    告警信息如果不拆的话，告警信息只存一个指令的UUID。无法查询拆分后的多个指令对应哪个告警信息。
                    拆分成多个指令也不好研判下发。
                    如果说直接研判告警信息的话，多个告警信息还可能对应一个指令，下发了这个告警信息，另一个下发吗？所以研判的应该是指令

     */
    @Override
    @GetMapping("/staticrule/check/{staticRuleIds}")
    public ResponseResult whiteListStaticRulesCheck(@PathVariable List<Integer> staticRuleIds) {
        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListService.whiteListStaticRulesCheck(staticRuleIds));
    }

    /*
    白名单数据统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult getWhiteListStatisticsData() {
        return ResponseResult.ok()
                .setData("whiteobj_num", whiteListService.queryWhiteListTotalNum(null, null, null, null, null))
                .setData("whiteobj_audit_num", whiteListService.queryAuditWhiteListTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())
                ))
                .setData("whiteobj_unaudit_num", whiteListService.queryAuditWhiteListTotalNum(
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())
                ))
                .setData("whiteobj_returned_num", whiteListService.queryAuditWhiteListTotalNum(
                       AuditStatusEnum.RETURNED.getNum()
                ));
    }

    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateWhiteListAuditStatusBatch(@Autowired HttpServletRequest request,
            @RequestBody Map<Integer, Integer> idsWithAuditStatusMap) {
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
                .setData("success", whiteListService.updateAuditStatusBatch(idsWithAuditStatusMap,
                        auditUserName, auditUserId, auditUserDepart));
    }

    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", whiteListService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", whiteListService.queryAuditInfo(id));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseResult queryHistory(@PathVariable Integer id,
                                       @RequestParam(value = "page", required = true) Integer page,
                                       @RequestParam(value = "page_size", required = true) Integer pageSize) {

        return ResponseResult.ok()
                .setData("history", whiteListService.queryHistory(id, page, pageSize));
    }


}
