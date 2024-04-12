package com.toomet.chat.config;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
