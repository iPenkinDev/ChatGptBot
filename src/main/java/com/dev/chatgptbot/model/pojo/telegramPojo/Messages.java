package com.dev.chatgptbot.model.pojo.telegramPojo;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Data
public class Messages {
    private int message_id;
    private From from;
    private int date;
    private Chat chat;
    private String text;
}