package com.TooMeet.Post.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Value("${cloudinary.cloud-name}")
    private String CLOUD_NAME;
    @Value("${cloudinary.api-key}")
    private String API_KEY;
    @Value("${cloudinary.api-secret}")
    private String API_SECRET;
    @Value("cloudinary.max_file_size")
    private String MAX_FILE_SIZE;

    @Bean
    public Cloudinary cloudinary(){
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name",CLOUD_NAME);
        config.put("api_key",API_KEY);
        config.put("api_secret",API_SECRET);
        config.put("max_file_size", String.valueOf(MAX_FILE_SIZE));
        config.put("secure", String.valueOf(true));
        return new Cloudinary(config);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
