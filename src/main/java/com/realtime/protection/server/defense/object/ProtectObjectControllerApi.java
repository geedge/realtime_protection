package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "proobj_name": "静态对象测试",
                                                        "success": true,
                                                        "proobj_id": 14542
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "proobj_name": 防护对象名称
                                                    
                                                    "success": 新建防护对象是否成功
                                                    
                                                    "proobj_id": 新建防护对象ID
                                                    """
                                    )
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
            @NotNull(message = "uploadFile字段不能为空") MultipartFile uploadFile
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
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "proobj_list": [
                                                          {
                                                            "proobj_id": 6068,
                                                            "proobj_name": "x-2-2",
                                                            "proobj_system_name": "xxxx system",
                                                            "proobj_port": 11,
                                                            "proobj_url": "alice.bob.com",
                                                            "proobj_protocol": "UDP",
                                                            "proobj_audit_status": 1
                                                          },
                                                          {
                                                            "proobj_id": 6069,
                                                            "proobj_name": "x-2-2",
                                                            "proobj_system_name": "xxxx system",
                                                            "proobj_port": 11,
                                                            "proobj_url": "alice.bob.com",
                                                            "proobj_protocol": "UDP",
                                                            "proobj_audit_status": 0
                                                          }
                                                        ],
                                                        "total_num": 8470
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "proobj_id": 防护对象ID
                                                    
                                                    "proobj_name": 防护对象名称
                                                    
                                                    "proobj_system_name": 防护对象操作系统
                                                    
                                                    "proobj_ip_address": 防护对象IP地址
                                                    
                                                    "proobj_port": 防护对象端口
                                                    
                                                    "proobj_url": 防护对象URL
                                                    
                                                    "proobj_protocol": 防护对象协议
                                                    
                                                    "proobj_audit_status": 防护对象审核状态
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "proobj_name", description = "防护对象名称"),
                    @Parameter(name = "proobj_id", description = "防护对象ID"),
                    @Parameter(name = "proobj_system_name", description = "防护对象操作系统"),
                    @Parameter(name = "proobj_ip_address", description = "防护对象IP地址"),
                    @Parameter(name = "proobj_port_min", description = "防护对象端口最小值"),
                    @Parameter(name = "proobj_port_max", description = "防护对象端口最大值"),
                    @Parameter(name = "proobj_url", description = "防护对象URL"),
                    @Parameter(name = "proobj_protocol", description = "防护对象网络协议"),
                    @Parameter(name = "proobj_create_username", description = "防护对象创建人"),
                    @Parameter(name = "proobj_audit_status", description = "防护对象审核状态"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页的对象个数", example = "5")
            }
    )
    ResponseResult queryProtectObjects(@RequestParam(value = "proobj_name", required = false)
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
                                       @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{protectObjectId}/query")
    @Operation(
            summary = "查询单个防护对象",
            description = "根据ID查询单个防护对象的信息",
            responses = {
                    @ApiResponse(
                            description = "返回单个防护对象信息",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "protect_object": {
                                                          "proobj_id": 14544,
                                                          "proobj_name": "静态对象测试",
                                                          "proobj_system_name": "xxx操作系统",
                                                          "proobj_port": 8080,
                                                          "proobj_url": "alice.bob.com",
                                                          "proobj_protocol": "TCP",
                                                          "proobj_audit_status": 0
                                                        }
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "proobj_id": 防护对象ID
                                                    
                                                    "proobj_name": 防护对象名称
                                                    
                                                    "proobj_system_name": 防护对象操作系统
                                                    
                                                    "proobj_ip_address": 防护对象IP地址
                                                    
                                                    "proobj_port": 防护对象端口
                                                    
                                                    "proobj_url": 防护对象URL
                                                    
                                                    "proobj_protocol": 防护对象协议
                                                    
                                                    "proobj_audit_status": 防护对象审核状态
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "protectObjectId", description = "防护对象ID", example = "2")
            }
    )
    ResponseResult queryProtectObject(@PathVariable Integer protectObjectId) throws IllegalAccessException;

    @PostMapping("/{protectObjectId}/update")
    @Operation(
            summary = "更新防护对象",
            description = "根据防护对象ID和信息更新防护对象",
            responses = {
                    @ApiResponse(
                            description = "防护对象更新情况",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true,
                                                        "proobj_id": 14544
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 防护对象更新是否成功
                                                    
                                                    "proobj_id": 防护对象ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "protectObjectId", description = "防护对象ID", example = "2")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "防护对象更新信息")
    )
    ResponseResult updateProtectObject(@PathVariable Integer protectObjectId,
                                       @RequestBody @Valid ProtectObject protectObject);


    @DeleteMapping("/{protectObjectId}/delete")
    @Operation(
            summary = "删除防护对象",
            description = "根据防护对象ID删除对应防护对象",
            responses = {
                    @ApiResponse(
                            description = "防护对象删除情况",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true,
                                                        "proobj_id": 14544
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 防护对象删除是否成功
                                                    
                                                    "proobj_id": 删除的防护对象ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "protectObjectId", description = "欲删除的防护对象ID", example = "2")
            }
    )
    ResponseResult deleteProtectObject(@PathVariable Integer protectObjectId);

    @DeleteMapping("/delete/{protectObjectIds}")
    @Operation(
            summary = "批量删除防护对象",
            description = "根据多个对象ID删除多个防护对象",
            responses = {
                    @ApiResponse(
                            description = "防护对象删除情况",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true,
                                                        "proobj_ids": [
                                                          14543,
                                                          14542
                                                        ]
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 防护对象删除是否成功
                                                    
                                                    "proobj_ids": 删除的防护对象ID列表
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "protectObjectIds", description = "欲删除的多个防护对象ID", example = "1,2,3,4")
            }
    )
    ResponseResult deleteProtectObject(@PathVariable List<Integer> protectObjectIds);

    @GetMapping("/{protectObjectId}/audit/{auditStatus}")
    @Operation(
            summary = "修改防护对象审核状态",
            description = "修改指定防护对象ID对应的防护对象的审核状态",
            responses = {
                    @ApiResponse(
                            description = "防护对象审核状态修改情况",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true,
                                                        "proobj_id": 14541,
                                                        "audit_status": 2
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 审核状态修改是否成功
                                                    
                                                    "proobj_id": 防护对象ID
                                                    
                                                    "audit_status": 防护对象当前审核状态
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "protectObjectId", description = "欲修改的防护对象ID", example = "2"),
                    @Parameter(name = "auditStatus", description = "欲修改的审核状态", example = "2")
            }
    )
    ResponseResult changeProtectObjectAuditStatus(@PathVariable Integer protectObjectId,
                                                  @PathVariable Integer auditStatus);

    /*
        防护对象数据统计
         */
    @Operation(
            summary = "数据统计",
            description = "数据统计",
            responses = {
                    @ApiResponse(
                            description = "返回数据统计",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "example",
                                            value = """
                                                    {
                                                         "code": 200,
                                                         "message": "request succeed",
                                                         "data": {
                                                           "proobj_num": 2,
                                                           "proobj_used_num": 2,
                                                           "proobj_audit_num": 1,
                                                           "proobj_undit_num": 0
                                                         }
                                                       }
                                                    """
                                    )
                            )
                    )
            },
            parameters = {

            }
    )
    @GetMapping("/statistics")
    ResponseResult statisticsProtectObject();

    @PostMapping("/auditbatch")
    ResponseResult updateWhiteListAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap);
}
