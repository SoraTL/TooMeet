package com.toomet.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TooMeet - Chat service API")
                        .description("Chat service rest api.")
                        .version("1.0")
                        .contact(new Contact().name("MinhHieuano").email("minh.hieu.a.n.o.n.y@gmail.com"))
                );

    }
}
