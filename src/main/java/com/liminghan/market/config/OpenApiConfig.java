package com.liminghan.market.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusMarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Market Admin API")
                        .description("APIs for campus market admin.")
                        .version("v1"));
    }
}
