package com.toomeet.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class SocketApplication implements CommandLineRunner {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
