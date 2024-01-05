package com.realtime.protection.server.defense.object;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PutMapping("/new")
    public ResponseResult newProtectObjectFromFile(MultipartFile updateFile) throws IOException {
        EasyExcel.read(updateFile.getInputStream(), ProtectObject.class,
                new ProjectObjectDataListener(protectObjectService)).sheet().doRead();
        return ResponseResult.ok();
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
    public ResponseResult deleteProtectObject(@RequestBody @JsonProperty("proobj_ids") List<Integer> protectObjectIds) {
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
