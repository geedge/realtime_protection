package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "白名单API", description = "白名单模块所有接口")
public interface WhiteListControllerApi {

    @Operation(
            summary = "新增白名单",
            description = "新增一个白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回新增对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class),
                                    examples = {@ExampleObject(
                                            value = """
                                                    {
                                                      "code": 200,
                                                      "message": "request succeed",
                                                      "data": {
                                                        "whiteobj_name": "白名单测试",
                                                        "success": true,
                                                        "whiteobj_id": 7026
                                                      }
                                                    }"""
                            )}
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "白名单信息")
    )
    ResponseResult newWhitelistObject(@RequestBody WhiteListObject object);

    @Operation(
            summary = "批量导入白名单",
            description = "以Excel文件方式批量导入白名单",
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
    ResponseResult uploadFile(MultipartFile uploadFile) throws IOException;

    @Operation(
            summary = "下载白名单模板",
            description = "下载白名单模板文件",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回下载结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseResult.class)

                            )
                    )
            }
    )
    void downloadTemplate(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "查询白名单",
            description = "按页和搜索内容查询白名单相关信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的所有白名单",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和WhiteListObject的属性",
                                            anyOf = {ResponseResult.class, WhiteListObject.class})
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "whiteobj_name", description = "白名单名称"),
                    @Parameter(name = "whiteobj_id", description = "白名单ID"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页查询个数", example = "10")
            }
    )
    ResponseResult queryWhiteListObject(@RequestParam(value = "whiteobj_name", required = false) String whiteListName,
                                        @RequestParam(value = "whiteobj_id", required = false) Integer whiteListId,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize);

    @Operation(
            summary = "查询单个白名单",
            description = "根据白名单ID查询单个白名单的所有详细信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的单个白名单",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和WhiteListObject的属性",
                                            anyOf = {ResponseResult.class, WhiteListObject.class})
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单ID", example = "2")
            }
    )
    ResponseResult queryWhiteListObjectById(@PathVariable Integer id);

    @Operation(
            summary = "删除白名单",
            description = "删除一个白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id")
            }
    )
    ResponseResult deleteWhiteListObjectById(@PathVariable Integer id);

    @Operation(
            summary = "批量删除白名单",
            description = "批量删除白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回删除对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "ids", description = "白名单id数组")
            }
    )
    ResponseResult deleteWhiteListObjects(@PathVariable List<Integer> whiteListObjIds);

    @Operation(
            summary = "修改白名单",
            description = "修改白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "白名单信息")

    )
    ResponseResult updateWhiteListObject(@PathVariable Integer id,
                                         @RequestBody WhiteListObject object);

    @Operation(
            summary = "修改白名单审核状态",
            description = "修改白名单审核状态",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回修改对象结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "id", description = "白名单id"),
                    @Parameter(name = "auditStatus", description = "白名单审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
            }
    )
    ResponseResult updateWhiteListObjectAuditStatus(@PathVariable Integer id,
                                                    @PathVariable Integer auditStatus);


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
    @Operation(
            summary = "查询静态规则命中白名单",
            description = "用于在创建任务的第三页中，查询静态规则命中的白名单",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回静态规则命中的所有白名单",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(title = "ResponseResult和WhiteListObject的属性",
                                            anyOf = {ResponseResult.class, WhiteListObject.class})
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "staticRuleIds", description = "静态规则id")
            }
    )
    ResponseResult whiteListStaticRulesCheck(@PathVariable List<Integer> staticRuleIds);
}
