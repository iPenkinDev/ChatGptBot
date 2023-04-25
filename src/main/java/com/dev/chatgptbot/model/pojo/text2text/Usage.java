package com.dev.chatgptbot.model.pojo.text2text;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class Usage {

    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}