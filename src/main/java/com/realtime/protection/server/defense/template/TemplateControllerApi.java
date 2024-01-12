package com.realtime.protection.server.defense.template;

import com.realtime.protection.configuration.entity.defense.template.Template;
import com.realtime.protection.configuration.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@Tag(name = "防御策略模板API", description = "防御策略模板模块所有接口")
public interface TemplateControllerApi {
    @PostMapping("/new")
    @Operation(
            summary = "新建防御策略模板",
            description = "根据信息新建一个防护策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回新建防御策略模板结果",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "防御策略模板信息")
    )
    ResponseResult newTemplate(@RequestBody @Valid Template template);

    @GetMapping("/query")
    @Operation(
            summary = "查询多个防御策略模板",
            description = "根据查询条件查询多个防御策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回所有查询到的防御策略模板",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseResult.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "template_name", description = "防御策略模板名称", example = "DDOS"),
                    @Parameter(name = "page", description = "页码", example = "1"),
                    @Parameter(name = "page_size", description = "每页对象数量", example = "5")
            }
    )
    ResponseResult queryTemplates(@RequestParam(value = "template_name", required = false) String templateName,
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
    ResponseResult queryTemplate(@PathVariable Integer templateId) throws IllegalAccessException;

    @PostMapping("/{templateId}/update")
    @Operation(
            summary = "更新防御策略模板信息",
            description = "根据提供的防御策略模板信息更新指定ID对应的防御策略模板",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板信息更新结果",
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
    ResponseResult updateTemplate(@PathVariable @Min(1) Integer templateId,
                                  @RequestBody @Valid Template template);

    @DeleteMapping("/{templateId}/delete")
    @Operation(
            summary = "删除防御策略模板信息",
            description = "根据策略模板ID删除指定的防御策略模板信息",
            responses = {
                    @ApiResponse(
                            description = "返回防御策略模板删除结果",
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
    ResponseResult deleteTemplate(@PathVariable @Min(1) Integer templateId);
}
