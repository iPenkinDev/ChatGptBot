package com.dev.chatgptbot.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Usage {

    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}