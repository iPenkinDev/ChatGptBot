package com.dev.chatgptbot.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Message {

    private String role;
    private String content;
}
