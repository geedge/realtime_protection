package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "静态规则API", description = "静态规则模块所有接口")
public interface StaticRuleControllerApi {

    @Operation(
            summary = "新增静态规则",
            description = "新增一个静态规则",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回新增对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "新增静态规则",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "static_rule_id": 1095,
                                                        "static_rule_name": "静态规则测试"
                                                      }
                                                    }""",
                                            description = "static_rule_id:新增静态规则名称" +
                                                    "static_rule_name:新增静态规则id"
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "静态规则信息")
    )
    ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object, HttpServletRequest request);

    @Operation(
            summary = "查询静态规则的附件信息",
            description = "查询静态规则的所有附件信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回静态规则的所有附件信息",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "查询静态规则的附件信息",
                                            value = """
                                                    {
                                                      {
                                                           "code": 200,
                                                           "message": "请求成功",
                                                           "data": {
                                                               "files": [
                                                                   {
                                                                       "id": 3,
                                                                       "fileName": "系统角色.docx",
                                                                       "filePath": null,
                                                                       "fileSize": 10915,
                                                                       "fileType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                                                       "staticRuleId": 1819
                                                                   },
                                                                   {
                                                                       "id": 4,
                                                                       "fileName": "权限.txt",
                                                                       "filePath": null,
                                                                       "fileSize": 100,
                                                                       "fileType": "text/plain",
                                                                       "staticRuleId": 1819
                                                                   }
                                                               ]
                                                           },
                                                           "another": null
                                                       }
                                                    }""",
                                            description = "static_rule_id:静态规则id" +
                                                    "id:文件id" + "fileName:文件名" + "filePath:文件路径" +
                                                    "fileSize:文件大小" + "fileType:文件类型" + "staticRuleId:静态规则id"
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "static_rule_id", description = "静态规则id")
            }
    )
    @GetMapping("/queryAttachment")
    ResponseResult queryAttachment(@RequestParam("static_rule_id") Long staticRuleId);

    //上传附件
    @Operation(
            summary = "上传附件",
            description = "以文件方式上传附件",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回上传结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "static_rule_id", description = "静态规则id"),
            }

    )
    @PostMapping("/uploadAttachment")
    ResponseResult uploadAttachment(@RequestParam("static_rule_id") Long staticRuleId,
                                    MultipartFile file) throws IOException;

    //文件下载
    @Operation(
            summary = "下载附件",
            description = "下载附件",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回下载结果",
                            content = @Content(
                                    mediaType = "application/octet-stream",
                                    schema = @Schema(implementation = ResponseEntity.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "文件")
            }
    )
    @GetMapping("downAttachment")
    ResponseEntity<Resource> downFile(@RequestParam Integer id) throws MalformedURLException, UnsupportedEncodingException;

    @Operation(
            summary = "批量导入静态规则",
            description = "以Excel文件方式批量导入静态规则",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回导入结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Excel文件")
    )
    ResponseResult uploadFile(@NotNull(message = "uploadFile字段不能为空") MultipartFile uploadFile,
                              @Autowired HttpServletRequest request) throws IOException;

    @Operation(
            summary = "下载静态规则模板",
            description = "下载静态规则模板文件",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回下载结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    void downloadTemplate(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "删除静态规则",
            description = "删除多个静态规则",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "删除多个静态规则",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "success": true,
                                                        "static_rule_id": [
                                                          11
                                                        ]
                                                      }
                                                    }""",
                                            description = "static_rule_id:删除静态规则id列表" +
                                                    "success:是否成功"
                                    )

                            )
                    )
            },
            parameters = {
                    @Parameter(name = "ids", description = "静态规则id数组")
            }
    )
    ResponseResult delete(@PathVariable List<Integer> ids);

    @Operation(
            summary = "修改静态规则",
            description = "修改静态规则",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            name = "修改静态规则",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {"success": true,}
                                                    }"""

                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "静态规则id")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "静态规则信息")
    )
    ResponseResult updateStaticRule(@PathVariable Integer id,
                                    @RequestBody @Valid StaticRuleObject object);

    @Operation(
            summary = "查询单个静态规则",
            description = "根据静态规则ID查询单个静态规则的所有详细信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的单个静态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和StaticRule的对象属性",
                                            anyOf = {ResponseResult.class, StaticRuleObject.class})
                            )
                    )
            },
            parameters = {@Parameter(name = "id", description = "静态规则ID", example = "38")}
    )
    ResponseResult queryStaticRuleById(@PathVariable Integer id);

    @Operation(
            summary = "分页查询静态规则",
            description = "根据查询条件和页码等，查询多个对象并以列表返回",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回多个静态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            title = "ResponseResult和StaticRule的对象属性",
                                            anyOf = {ResponseResult.class, StaticRuleObject.class}),
                                    examples = @ExampleObject(
                                            name = "分页查询静态规则",
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "static_rule_list": [
                                                        ],
                                                        "static_rule_total_num": 1080
                                                      }
                                                    }""",

                                            description = "static_rule_list:静态规则列表" +
                                                    "static_rule_total_num:静态规则总数"
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "name", description = "静态规则名称"),
                    @Parameter(name = "id", description = "静态规则id"),
                    @Parameter(name = "creator", description = "静态规则创建人"),
                    @Parameter(name = "sip", description = "静态规则ip，匹配源ip并考虑掩码"),
                    @Parameter(name = "sport", description = "静态规则port，匹配源port"),
                    @Parameter(name = "dip", description = "静态规则ip，匹配目的ip并考虑掩码"),
                    @Parameter(name = "dport", description = "静态规则port，匹配目的port"),
                    @Parameter(name = "auditStatus", description = "静态规则审核状态"),
                    @Parameter(name = "auditUserName", description = "静态规则审核用户名称"),
                    @Parameter(name = "createTime", description = "静态规则创建时间"),
                    @Parameter(name = "page", description = "页码"),
                    @Parameter(name = "pageSize", description = "每页条数")
            }
    )
    ResponseResult queryStaticRule(@RequestParam(required = false) String static_rule_name,
                                   @RequestParam(required = false) String static_rule_id,
                                   @RequestParam(required = false) String static_rule_creator,
                                   @RequestParam(required = false) String static_rule_sip,
                                   @RequestParam(required = false) Integer static_rule_sport,
                                   @RequestParam(required = false) String static_rule_dip,
                                   @RequestParam(required = false) Integer static_rule_dport,
                                   @RequestParam(required = false) Integer auditStatus,
                                   @RequestParam(required = false) String auditUserName,
                                   @RequestParam(required = false) LocalDate createTime,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize
    );


    @Operation(summary = "批量查询静态规则",
            description = "根据静态规则ID列表查询多个静态规则的所有详细信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的多个静态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和StaticRule的对象属性",
                                            anyOf = {ResponseResult.class, StaticRuleObject.class})
                            )
                    )
            },
            parameters = {@Parameter(name = "ids", description = "静态规则ID列表", example = "staticrule/query/15,16,17")}
    )

    @GetMapping("/query/{ids}")
    ResponseResult queryStaticRuleByIds(@PathVariable List<Integer> ids);

    @Operation(
            summary = "按id删除静态规则",
            description = "按id删除静态规则",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {}
                                                    }"""
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "静态规则id")
            }
    )
    ResponseResult deleteStaticRule(@PathVariable Integer id);


    @Operation(
            summary = "更新静态规则审批状态",
            description = "更新静态规则审批状态，0未审核、1审核不通过、2审核通过",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回静态规则审核结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "静态规则id"),
                    @Parameter(name = "auditStatus", description = "要修改为的静态规则审核状态")
            }
    )
    ResponseResult updateStaticRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus,
                                               @Autowired HttpServletRequest request);

    @PostMapping("/auditbatch")
    @Operation(
            summary = "批量更新静态规则审批状态",
            description = "批量更新静态规则审批状态，0未审核、1审核不通过、2审核通过",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回静态规则审核结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "字典，key是静态规则id，value是静态规则审核状态id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)

                    )
            )
    )
    ResponseResult updateStaticRuleAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap,
                                                    @Autowired HttpServletRequest request);

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
                                                         "static_rule_num": 7,
                                                         "static_rule_used_num": 2,
                                                         "static_rule_audit_num": 3,
                                                         "static_rule_unaudit_num": 1
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
    ResponseResult getStaticRuleStatisticsData();

    @Operation(
            summary = "更新审批意见",
            description = "批量更新审批意见，接收多个id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回是否成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "字典，key是'audit_info'，value是审核意见",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "example",
                                    value = """
                                    {
                                         "audit_info": "审核不通过，原因是xxxx"
                                       }
                                    """
                            )

                    )
            ),
            parameters = {
                    @Parameter(name = "ids", description = "欲更新的ID", example = "2,3"),
            }
    )
    @PostMapping("/auditInfo/{ids}")
    ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                   @RequestBody Map<String, String> auditInfo);


    @Operation(
            summary = "查询审批意见",
            description = "查询审批意见，只接收一个id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回是否成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "查询的ID", example = "2"),
            }
    )
    @GetMapping("/auditInfo/{id}")
    ResponseResult queryAuditInfo(@PathVariable Integer id);



    @Operation(
            summary = "查询历史变化",
            description = "查询历史变化，只接收一个id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回是否成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "page", description = "页数", example = "2"),
                    @Parameter(name = "page_size", description = "每页个数", example = "10"),
            }
    )
    @GetMapping("/{id}/history")
    ResponseResult queryHistory(@PathVariable Integer id,
                                @RequestParam(value = "page", required = true) Integer page,
                                @RequestParam(value = "page_size", required = true) Integer pageSize);

}
