package com.dev.chatgptbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageService {

    SendMessage sendMessage (Message message);

    String getResponse(String message) throws JsonProcessingException;
}
