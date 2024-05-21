package com.realtime.protection.server.defense.templatenew;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.entity.defense.template.TemplateNew;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Tag(name = "策略模板API", description = "策略模板模块所有接口")
public interface TemplateNewCpntrollerApi {


    @PostMapping("/new")
    @Operation(
            summary = "新建防御策略模板",
            description = "根据信息新建一个防护策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回新建防御策略模板结果",
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
                                                        "template_id": 1442
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 新建防御策略模板是否成功
                                                    "template_id": 新建防御策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TemplateNew.class)
                    ),
                    description = "防御策略模板信息")

    )
    ResponseResult newTemplate(@RequestBody @Valid TemplateNew template);

    @GetMapping("/query")
    @Operation(
            summary = "分頁查询策略模板",
            description = "分頁查询策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回策略模板防护等级信息",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和Template的属性",
                                            anyOf = {ResponseResult.class, TemplateNew.class})

                            )
                    )
            },
            parameters = {
            @Parameter(name = "template_name", description = "防御策略模板名称", example = "DDOS"),
            @Parameter(name = "source_system", description = "策略模板来源那系统", example = "xxx系统"),
            @Parameter(name = "create_user_name", description = "创建人名称", example = "xxx"),
            @Parameter(name = "template_id", description = "防御策略模板ID", example = "1"),
            @Parameter(name = "audit_status", description = "审核状态", example = "0"),
            @Parameter(name = "event_type", description = "事件类型", example = "ddos"),
            @Parameter(name = "protect_level", description = "防护等级", example = "1"),
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "page_size", description = "每页对象数量", example = "5")
    }
    )
    ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
                                  @RequestParam(value = "source_system", required = false) String sourceSystem,
                                  @RequestParam(value = "create_user_name", required = false) String createUserName,
                                  @RequestParam(value = "template_id", required = false) Integer templateId,
                                  @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                  @RequestParam(value = "event_type", required = false) String eventType,
                                  @RequestParam(value = "protect_level", required = false) Integer protectLevel,

                                  @RequestParam("page") @Min(1) Integer page,
                                  @RequestParam("page_size") @Min(1) Integer pageSize);

    @GetMapping("/{templateId}/query")
    @Operation(
            summary = "查询单个防御策略模板",
            description = "根据模板ID查询单个防御策略模板信息",
            responses = {
                    @ApiResponse(
                            description = "返回单个防御策略模板信息",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            }
    )
    ResponseResult queryTemplate(@PathVariable Integer templateId);


    @PostMapping("/{templateId}/update")
    @Operation(
            summary = "更新防御策略模板信息",
            description = "根据提供的防御策略模板信息更新指定ID对应的防御策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板信息更新结果",
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
                                                        "template_id": 262
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 更新是否成功
                                                    
                                                    "template_id": 更新的策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TemplateNew.class)
                    ),
                    description = "防御策略模板信息")
    )
    ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                  @RequestBody @Valid TemplateNew template);

    @DeleteMapping("/{templateId}/delete")
    @Operation(
            summary = "删除防御策略模板信息",
            description = "根据策略模板ID删除指定的防御策略模板信息",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板删除结果",
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
                                                        "template_id": 262
                                                      }
                                                    }
                                                    """,
                                            description = """
                                                    "success": 删除是否成功
                                                    
                                                    "template_id": 删除的策略模板ID
                                                    """
                                    )
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "templateId", description = "防御策略模板ID", example = "5")
            }
    )
    ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId);
    @Operation(
            summary = "查询来源系统名称",
            description = "查询策略模板中的所有来源系统名称",
            responses = {
                    @ApiResponse(
                            description = "返回来源系统名称查询结果，返回字符串列表",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            }
    )
    @GetMapping("/query/source_system")
    ResponseResult querySourceSystem();

//    @GetMapping("/query/event_name/{sourceSystem}")
//    ResponseResult queryEventName(@PathVariable String sourceSystem);

    /*
        策略模板数据统计
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
                                                          "template_num": 1,
                                                          "template_used_num": 1,
                                                          "template_audit_num": 0,
                                                          "template_unaudit_num": 1
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
    ResponseResult statisticsTemplate();

    @Operation(
            summary = "按id修改审计状态",
            description = "按id修改审计状态",
            responses = {
                    @ApiResponse(
                            description = "返回数据统计",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)

                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "防御策略模板ID", example = "5"),
                    @Parameter(name = "auditStatus", description = "修改为的审批状态", example = "2")
            }
    )
    @GetMapping("/{id}/audit/{auditStatus}")
    ResponseResult updateTemplateAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus);
    @Operation(
            summary = "批量更新审批状态",
            description = "批量更新审批状态，0未审核、1审核不通过、2审核通过",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回审核结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "字典，key是id，value是审核状态",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)

                    )
            )
    )
    @PostMapping("/auditbatch")
    ResponseResult updateWhiteListAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap);

    /*
        修改审批信息
         */
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
                                         "auditInfo": "审核不通过，原因是xxxx"
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
}
