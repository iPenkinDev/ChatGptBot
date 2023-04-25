package com.dev.chatgptbot.model.pojo.text2text;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class Message {

    private String role;
    private String content;
}
