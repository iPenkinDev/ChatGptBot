package com.dev.chatgptbot.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TelegramBotUtils {

    private final String BOT_GET_VOICE_URL = "https://api.telegram.org/file/bot";
}
