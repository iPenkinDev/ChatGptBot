package com.dev.chatgptbot.model.pojo.telegramPojo;

import lombok.Data;

@Data
public class From {
    private long id;
    private String first_name;
    private boolean is_bot;
    private String username;
    private String language_code;
}
