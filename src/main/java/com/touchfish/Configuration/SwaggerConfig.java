package com.touchfish.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearer-key", in = SecuritySchemeIn.HEADER,bearerFormat = "JWT",scheme = "bearer")
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API文档")
                        .description("项目的API文档，测试接口")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));


    }


}
