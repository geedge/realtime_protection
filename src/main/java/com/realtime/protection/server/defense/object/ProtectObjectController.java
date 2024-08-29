package com.realtime.protection.server.defense.object;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.response.ResponseResult;
                                                                            import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/proobj")
public class ProtectObjectController implements ProtectObjectControllerApi {

    private final ProtectObjectService protectObjectService;

    public ProtectObjectController(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

    @Override
    @PostMapping("/new")
    public ResponseResult newProtectObject(@RequestBody @Valid ProtectObject protectObject,
                                           @Autowired HttpServletRequest request) {
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            protectObject.setProtectObjectCreateUsername(user.name);
            protectObject.setProtectObjectCreateUserId(Integer.valueOf(user.uid));
            protectObject.setProtectObjectCreateDepart(user.getOrgName());
        }
        Integer protectObjectId = protectObjectService.newProtectObject(protectObject);
        if (protectObjectId == 0) {
            return ResponseResult.error().setMessage("failed to create a protect object")
                    .setData("proobj_name", protectObject.getProtectObjectName())
                    .setData("proobj_id", protectObjectId)
                    .setData("success", false);
        }

        return ResponseResult.ok()
                .setData("proobj_name", protectObject.getProtectObjectName())
                .setData("proobj_id", protectObject.getProtectObjectId())
                .setData("success", true);
    }

    @Override
    @PostMapping("/upload")
    public ResponseResult uploadFile(
            @NotNull(message = "uploadFile字段不能为空") MultipartFile uploadFile,
            @Autowired HttpServletRequest request
    ) throws IOException {
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        EasyExcel.read(uploadFile.getInputStream(), ProtectObject.class,
                new ProjectObjectDataListener(protectObjectService, user)).sheet().doRead();
        return ResponseResult.ok();
    }

    @Override
    @GetMapping("/download")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("防护对象上传模板", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), ProtectObject.class)
                .sheet("防护对象上传模板")
                .doWrite(List.of());
    }

    @Override
    @GetMapping("/query")
    public ResponseResult queryProtectObjects(@RequestParam(value = "proobj_name", required = false)
                                              String protectObjectName,
                                              @RequestParam(value = "proobj_id", required = false)
                                              String protectObjectId,
                                              @RequestParam(value = "proobj_systemm_name", required = false)
                                              String protectObjectSystemName,
                                              @RequestParam(value = "proobj_ip_address", required = false)
                                              String protectObjectIPAddress,
                                              @RequestParam(value = "proobj_port_min", required = false)
                                              Integer protectObjectPortMin,
                                              @RequestParam(value = "proobj_port_max", required = false)
                                              Integer protectObjectPortMax,
                                              @RequestParam(value = "proobj_url", required = false)
                                              String protectObjectURL,
                                              @RequestParam(value = "proobj_protocol", required = false)
                                              String protectObjectProtocol,
                                              @RequestParam(value = "proobj_create_username", required = false)
                                              String protectObjectCreateUsername,
                                              @RequestParam(value = "proobj_audit_status", required = false)
                                              Integer protectObjectAuditStatus,
                                              @RequestParam("page") @Min(1) Integer page,
                                              @RequestParam("page_size") @Min(1) Integer pageSize) {
        return ResponseResult.ok()
                .setData("proobj_list", protectObjectService.queryProtectObjects(
                        protectObjectName, protectObjectId, protectObjectSystemName, protectObjectIPAddress,
                        protectObjectPortMin, protectObjectPortMax, protectObjectURL, protectObjectProtocol,
                        protectObjectCreateUsername, protectObjectAuditStatus,
                        page, pageSize))
                .setData("total_num", protectObjectService.queryProtectObjectsTotalNum(
                        protectObjectName, protectObjectId, protectObjectSystemName, protectObjectIPAddress,
                        protectObjectPortMin, protectObjectPortMax, protectObjectURL, protectObjectProtocol,
                        protectObjectCreateUsername, protectObjectAuditStatus
                ));
    }

    @Override
    @GetMapping("/{protectObjectId}/query")
    public ResponseResult queryProtectObject(@PathVariable Integer protectObjectId) {
        ProtectObject protectObject = protectObjectService.queryProtectObject(protectObjectId);
        if (protectObject == null) {
            return ResponseResult.invalid()
                    .setMessage("无效的防护对象ID，也许该ID指定的防护对象不存在？");
        }
        return ResponseResult.ok().setData("protect_object", protectObject);
    }

    @Override
    @GetMapping("/{protectObjectIds}/querybatch")
    public ResponseResult queryProtectObject(@PathVariable List<Integer> protectObjectIds) {
        List<ProtectObject> protectObjects = protectObjectService.queryProtectObjectByIds(protectObjectIds);
        if (protectObjects == null) {
            return ResponseResult.invalid()
                    .setMessage("无效的防护对象ID，也许该ID指定的防护对象不存在？");
        }
        return ResponseResult.ok().setData("protect_object", protectObjects);
    }

    @Override
    @PostMapping("/{protectObjectId}/update")
    public ResponseResult updateProtectObject(@PathVariable Integer protectObjectId,
                                              @RequestBody @Valid ProtectObject protectObject) {
        protectObject.setProtectObjectId(protectObjectId);
        return ResponseResult.ok()
                .setData("proobj_id", protectObject.getProtectObjectId())
                .setData("success", protectObjectService.updateProtectObject(protectObject));
    }

    @Override
    @DeleteMapping("/{protectObjectId}/delete")
    public ResponseResult deleteProtectObject(@PathVariable Integer protectObjectId) {
        return ResponseResult.ok()
                .setData("proobj_id", protectObjectId)
                .setData("success", protectObjectService.deleteProtectObject(protectObjectId));
    }

    @Override
    @DeleteMapping("/delete/{protectObjectIds}")
    public ResponseResult deleteProtectObject(@PathVariable List<Integer> protectObjectIds) {
        return ResponseResult.ok()
                .setData("proobj_ids", protectObjectIds)
                .setData("success", protectObjectService.deleteProtectObjects(protectObjectIds));
    }

    @Override
    @GetMapping("/{protectObjectId}/audit/{auditStatus}")
    public ResponseResult changeProtectObjectAuditStatus(@PathVariable Integer protectObjectId,
                                                         @PathVariable Integer auditStatus,
                                                         @Autowired HttpServletRequest request) {
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
                .addDataMap(protectObjectService.changeProtectObjectAuditStatus(
                        protectObjectId, auditStatus, auditUserName, auditUserId, auditUserDepart))
                .setData("proobj_id", protectObjectId);
    }

    /*
    防护对象数据统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult statisticsProtectObject() {
        return ResponseResult.ok()
                .setData("proobj_num", protectObjectService.queryProtectObjectsTotalNum(null, null, null, null,
                        null, null, null, null, null, null))
                .setData("proobj_used_num", protectObjectService.queryUsedProtectObjectsTotalNum())
                .setData("proobj_audit_num", protectObjectService.queryProtectObjectsTotalNum(null, null, null, null,
                        null, null, null, null, null,
                        AuditStatusEnum.getNumByState(AuditStatusEnum.AUDITED.getState())))
                .setData("proobj_undit_num", protectObjectService.queryProtectObjectsTotalNum(null, null, null, null,
                        null, null, null, null, null,
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())))
                .setData("proobj_returned_num", protectObjectService.queryProtectObjectsTotalNum(null, null, null, null,
                        null, null, null, null, null,
                        AuditStatusEnum.getNumByState(AuditStatusEnum.RETURNED.getState())));
    }


    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateWhiteListAuditStatusBatch(
            @RequestBody Map<Integer, Integer> idsWithAuditStatusMap,
            @Autowired HttpServletRequest request) {
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
          //  从http首部session字段获取用户信息
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
                .setData("success", protectObjectService.updateAuditStatusBatch(
                        idsWithAuditStatusMap, auditUserName, auditUserId, auditUserDepart));
    }

    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", protectObjectService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", protectObjectService.queryAuditInfo(id));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseResult queryHistory(@PathVariable Integer id,
                                       @RequestParam(value = "page", required = true) Integer page,
                                       @RequestParam(value = "page_size", required = true) Integer pageSize) {

        return ResponseResult.ok()
                .setData("history", protectObjectService.queryHistory(id, page, pageSize));
    }

    @Override
    @GetMapping("/synchronize")
    public ResponseResult synchronizeProtectObject() {

        return ResponseResult.ok()
                .setData("success", true)
                .setData("synchronize", 145);
    }

}
