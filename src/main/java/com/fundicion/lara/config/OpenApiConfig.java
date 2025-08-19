package com.fundicion.lara.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Fundici\u00F3n Lara API")
                .description("Web API de Fundici\u00F3n Lara")
                .contact(new Contact().email("roberto.aav.23@gmail.com").name("GDB"))
                // .license(new License().name("Lara 2025").url(""))
                .version("0.0.1");
    }


}
