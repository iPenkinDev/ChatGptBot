package com.dev.chatgptbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatGptBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptBotApplication.class, args);

    }

}
