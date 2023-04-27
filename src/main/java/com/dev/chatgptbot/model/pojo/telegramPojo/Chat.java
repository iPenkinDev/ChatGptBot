package com.dev.chatgptbot.model.pojo.telegramPojo;

import lombok.Data;

@Data
public class Chat {
    private int id;
    private String type;
    private String first_name;
    private String username;
}