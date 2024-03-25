package com.toomeet.gateway;

import com.toomeet.gateway.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication implements CommandLineRunner {

    @Autowired
    private JwtService jwtService;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
//        String token = jwtService.generateToken("my-id");
//        System.out.println("token = " + token);
    }
}
