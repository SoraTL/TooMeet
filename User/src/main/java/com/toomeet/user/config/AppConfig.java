package com.toomeet.user.config;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Bean
//    public WebMvcConfigurer corsConfig() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(@NonNull CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods(HttpMetho.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE)
//                        .allowedHeaders(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION);
//            }
//        };
//    }

}
