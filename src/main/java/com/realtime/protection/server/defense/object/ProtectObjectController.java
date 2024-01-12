package com.realtime.protection.server.defense.object;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.EntityUtils;
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
            @NotNull(message = "uploadFile cannot be null. ") MultipartFile uploadFile
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
                                              @RequestParam(value = "proobj_id", required = false) @Min(1)
                                              Integer protectObjectId,
                                              @RequestParam("page") @Min(1) Integer page,
                                              @RequestParam("page_size") @Min(1) Integer pageSize) {
        return ResponseResult.ok()
                .setData("proobj_list", protectObjectService.queryProtectObjects(protectObjectName,
                        protectObjectId, page, pageSize));

    }

    @Override
    @GetMapping("/{protectObjectId}/query")
    public ResponseResult queryProtectObject(@PathVariable Integer protectObjectId) throws IllegalAccessException {
        ProtectObject protectObject = protectObjectService.queryProtectObject(protectObjectId);
        return ResponseResult.ok()
                .setDataMap(EntityUtils.entityToMap(protectObject));
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
                .setDataMap(protectObjectService.changeProtectObjectAuditStatus(protectObjectId, auditStatus))
                .setData("proobj_id", protectObjectId);
    }
}
