package org.ohgiraffers.board.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Board Mission👹",
        description = "board Mission Api 명세",
        version = "v1")
)
@Configuration
//config 클래스라고 선언
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi firstOpenApi() {
        String[] path = {
                "org.ohgiraffers.board.controller"
        };

        return GroupedOpenApi.builder()
                .group("1. 게시글 관리")
                .packagesToScan(path)
                .build();
    }

}
