package com.dev.chatgptbot.model.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ChatCompletion {

    private String id;
    private String object;
    private long created;
    private String model;
    private Usage usage;
    private List<Choice> choices;
}