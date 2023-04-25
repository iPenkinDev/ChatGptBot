package com.dev.chatgptbot.model.pojo.text2text;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class Choice {
    private Message message;
    private String finishReason;
    private int index;
}