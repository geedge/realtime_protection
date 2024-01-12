package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "防护对象API", description = "防护对象模块所有接口")
public interface ProtectObjectControllerApi {
    @PostMapping("/new")
    @Operation(
            summary = "新建防护对象",
            description = "新建一个防护对象",
            responses = {
                    @ApiResponse(
                            description = "返回新建对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "防护对象信息")
    )
    ResponseResult newProtectObject(@RequestBody @Valid ProtectObject protectObject);

    @PostMapping("/upload")
    @Operation(
            summary = "批量上传防护对象",
            description = "使用模板文件上传并新建多个防护对象",
            responses = {
                    @ApiResponse(
                            description = "返回批量上传新建对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "上传文件")
    )
    ResponseResult uploadFile(
            @NotNull(message = "uploadFile cannot be null. ") MultipartFile uploadFile
    ) throws IOException;

    @GetMapping("/download")
    @Operation(
            summary = "下载模板文件",
            description = "下载防护对象上传模板文件",
            responses = {
                    @ApiResponse(
                            description = "返回防护对象模板文件",
                            content = @Content(
                                    mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                            )
                    )
            }
    )
    void downloadTemplate(HttpServletResponse response) throws IOException;

    @GetMapping("/query")
    @Operation(
            summary = "根据条件查询多个防护对象",
            description = "根据查询条件和页码等，查询多个对象并以列表返回",
            responses = {
                    @ApiResponse(
                            description = "返回多个防护对象",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter()
            }
    )
    ResponseResult queryProtectObjects(@RequestParam(value = "proobj_name", required = false)
                                       String protectObjectName,
                                       @RequestParam(value = "proobj_id", required = false) @Min(1)
                                       Integer protectObjectId,
                                       @RequestParam("page") @Min(1) Integer page,
                                       @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{protectObjectId}/query")
    ResponseResult queryProtectObject(@PathVariable Integer protectObjectId) throws IllegalAccessException;

    @PostMapping("/{protectObjectId}/update")
    ResponseResult updateProtectObject(@PathVariable Integer protectObjectId,
                                       @RequestBody @Valid ProtectObject protectObject);

    @DeleteMapping("/{protectObjectId}/delete")
    ResponseResult deleteProtectObject(@PathVariable Integer protectObjectId);

    @DeleteMapping("/delete/{protectObjectIds}")
    ResponseResult deleteProtectObject(@PathVariable List<Integer> protectObjectIds);

    @GetMapping("/{protectObjectId}/audit/{auditStatus}")
    ResponseResult changeProtectObjectAuditStatus(@PathVariable Integer protectObjectId,
                                                  @PathVariable Integer auditStatus);
}
