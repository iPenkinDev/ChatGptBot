package com.dev.chatgptbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramBotConfig {

    @Value("${bot_name}")
    private String botName;

    @Value("${bot_token}")
    private String botToken;
}
