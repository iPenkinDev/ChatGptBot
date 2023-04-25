package com.dev.chatgptbot.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ChatGptUtils {

    private final String GPT_SEND_MESSAGE_URL = "https://api.openai.com/v1/chat/completions";

    private final String GPT_SEND_VOICE_URL = "https://api.openai.com/v1/audio/transcriptions";
}
