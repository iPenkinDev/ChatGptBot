package com.dev.chatgptbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ChatGptConfig {

    @Value("${chat_gpt_token}")
    private String chatToken;
}
