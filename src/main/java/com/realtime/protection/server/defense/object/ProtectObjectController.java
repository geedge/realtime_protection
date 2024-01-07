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
public class ProtectObjectController {

    private final ProtectObjectService protectObjectService;

    public ProtectObjectController(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

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

    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), ProtectObject.class,
                new ProjectObjectDataListener(protectObjectService)).sheet().doRead();
        return ResponseResult.ok();
    }

    @GetMapping("/download")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("防护对象", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), ProtectObject.class)
                .sheet("防护对象")
                .doWrite(List.of());
    }

    @GetMapping("/query")
    public ResponseResult queryProtectObjects(@RequestParam(value = "proobj_name", required = false)
                                              String protectObjectName,
                                              @RequestParam(value = "proobj_id", required = false) @Min(1)
                                              Integer protectObjectId,
                                              @RequestParam("page") @Min(1) Integer page,
                                              @RequestParam("page_size") @Min(1) Integer pageSize) {
        return ResponseResult.ok()
                .setData("proobj_list", protectObjectService.queryProtectObjects(protectObjectName,
                        protectObjectId, page, pageSize));

    }

    @GetMapping("/{id}/query")
    public ResponseResult queryProtectObject(@PathVariable("id") Integer protectObjectId) {
        ProtectObject protectObject = protectObjectService.queryProtectObject(protectObjectId);
        return ResponseResult.ok()
                .setData("proobj_id", protectObject.getProtectObjectId())
                .setData("proobj_name", protectObject.getProtectObjectName())
                .setData("proobj_system_name", protectObject.getProtectObjectSystemName())
                .setData("proobj_ip_address", protectObject.getProtectObjectIPAddress())
                .setData("proobj_port", protectObject.getProtectObjectPort())
                .setData("proobj_url", protectObject.getProtectObjectURL())
                .setData("proobj_protocol", protectObject.getProtectObjectProtocol());
    }

    @PostMapping("/{id}/update")
    public ResponseResult updateProtectObject(@PathVariable("id") Integer protectObjectId,
                                              @RequestBody @Valid ProtectObject protectObject) {
        protectObject.setProtectObjectId(protectObjectId);
        return ResponseResult.ok()
                .setData("proobj_id", protectObject.getProtectObjectId())
                .setData("success", protectObjectService.updateProtectObject(protectObject));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseResult deleteProtectObject(@PathVariable("id") Integer protectObjectId) {
        return ResponseResult.ok()
                .setData("proobj_id", protectObjectId)
                .setData("success", protectObjectService.deleteProtectObject(protectObjectId));
    }

    @PostMapping("/delete")
    public ResponseResult deleteProtectObject(@RequestBody List<Integer> protectObjectIds) {
        return ResponseResult.ok()
                .setData("proobj_ids", protectObjectIds)
                .setData("success", protectObjectService.deleteProtectObjects(protectObjectIds));
    }

    @PostMapping("/{id}/audit/{status}")
    public ResponseResult changeProtectObjectAuditStatus(@PathVariable("id") Integer protectObjectId,
                                                         @PathVariable("status") Integer auditStatus) {
        return ResponseResult.ok()
                .setDataMap(protectObjectService.changeProtectObjectAuditStatus(protectObjectId, auditStatus))
                .setData("proobj_id", protectObjectId);
    }
}
