package com.realtime.protection.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "实时方宇项目",
                version = "1.0",
                description = "实时方宇项目——前端接口文档",
                contact = @Contact(name = "陈松岳", email = "chensongyue@iie.ac.cn")
        )
)
public class SwaggerConfiguration {

}
