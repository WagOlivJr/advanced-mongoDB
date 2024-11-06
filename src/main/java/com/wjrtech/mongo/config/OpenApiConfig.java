package com.wjrtech.mongo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Constraint;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI ContactOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Busca Prestadores por Rede")
                        .description("API que busca prestadores por raio de distância")
                        .version("v2.0")
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                ;
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringShop Wiki Documentation")
//                        .url("https://springshop.wiki.github.org/docs")
//                );
    }
}
