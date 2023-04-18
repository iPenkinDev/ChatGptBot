package com.dev.chatgptbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

public interface MessageService {

    SendMessage sendMessage (Message message);

    String sendRequest(String message);

    String sendVoiceRequest();
}
