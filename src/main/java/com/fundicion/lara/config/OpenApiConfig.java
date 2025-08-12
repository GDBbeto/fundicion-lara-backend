package com.fundicion.lara.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                .title("Usuarios Wealth Tech Web API")
                .description("Web API de usuarios del sistema Wealth Tech")
                .contact(new Contact().email("contacto@meltsan.com").name("Infosel Team"))
                .license(new License().name("Infosel 2021").url("https://www.infosel.com"))
                .version("0.0.1");
    }


}
