package com.realtime.protection.server.rule.staticrule;

import com.realtime.protection.configuration.entity.rule.staticrule.StaticRuleObject;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "静态规则信息")
    )
    ResponseResult newStaticRuleObject(@RequestBody @Valid StaticRuleObject object) ;

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
    ResponseResult uploadFile(MultipartFile uploadFile) throws IOException;

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
    void downloadTemplate(HttpServletResponse response) throws IOException ;

    @Operation(
            summary = "删除静态规则",
            description = "删除多个静态规则",
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
                    @Parameter(name = "ids", description = "静态规则id数组")
            }
    )
    ResponseResult delete(@PathVariable List<Integer> ids) ;

    @Operation(
            summary = "修改静态规则",
            description = "修改静态规则",
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
                    @Parameter(name = "id", description = "静态规则id")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "静态规则信息")
    )
    ResponseResult updateStaticRule(@PathVariable Integer id,
                                           @RequestBody @Valid StaticRuleObject object) ;

    @Operation(
            summary = "查询单个静态规则",
            description = "根据静态规则ID查询单个静态规则的所有详细信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回查询到的单个静态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {@Parameter(name = "id", description = "静态规则ID", example = "38")}
    )
    ResponseResult queryStaticRuleById(@PathVariable Integer id) ;

    @Operation(
            summary = "分页查询静态规则",
            description = "根据查询条件和页码等，查询多个对象并以列表返回",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "返回多个静态规则",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "static_rule_name", description = "静态规则名称"),
                    @Parameter(name = "static_rule_id", description = "静态规则id"),
                    @Parameter(name = "page", description = "页码"),
                    @Parameter(name = "pageSize", description = "每页条数")
            }
    )
    ResponseResult queryStaticRule(String static_rule_name, Integer static_rule_id,
                @RequestParam(defaultValue = "1") Integer page,
                @RequestParam(defaultValue = "10") Integer pageSize);


    @Operation(
            summary = "按id删除静态规则",
            description = "按id删除静态规则",
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
                    @Parameter(name = "id", description = "静态规则id")
            }
    )
    ResponseResult deleteStaticRule(@PathVariable Integer id) ;

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
    public ResponseResult updateStaticRuleAuditStatus(@PathVariable Integer id, @PathVariable Integer auditStatus) ;

    }
