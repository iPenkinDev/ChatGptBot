package com.dev.chatgptbot.model;

import com.dev.chatgptbot.service.MessageService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Log4j
public class ChatGpt {

    private final MessageService sendMessageService;


    public ChatGpt(MessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    public String sendMessageToChatGptBot(String text) {
        log.debug("sendMessageToChatGpt: " + text);
        return sendMessageService.sendRequest(text);
    }

    public String sendVoiceMessageToChatGptBot() {
        log.debug("sendVoiceMessageToChatGpt: ");
        return sendMessageService.sendVoiceRequest();
    }
}
