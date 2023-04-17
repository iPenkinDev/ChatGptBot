package com.dev.chatgptbot.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Choice {
    private Message message;
    private String finishReason;
    private int index;
}