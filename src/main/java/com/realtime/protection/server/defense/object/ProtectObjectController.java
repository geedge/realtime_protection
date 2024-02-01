package com.realtime.protection.server.defense.object;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/proobj")
public class ProtectObjectController implements ProtectObjectControllerApi {

    private final ProtectObjectService protectObjectService;

    public ProtectObjectController(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

    @Override
    @PostMapping("/new")
    public ResponseResult newProtectObject(@RequestBody @Valid ProtectObject protectObject) {
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
            @NotNull(message = "uploadFile字段不能为空") MultipartFile uploadFile
    ) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), ProtectObject.class,
                new ProjectObjectDataListener(protectObjectService)).sheet().doRead();
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
                                              @Min(value = 1, message = "ID值不可小于1")
                                              Integer protectObjectId,
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
                                                         @PathVariable Integer auditStatus) {
        return ResponseResult.ok()
                .addDataMap(protectObjectService.changeProtectObjectAuditStatus(protectObjectId, auditStatus))
                .setData("proobj_id", protectObjectId);
    }
}
