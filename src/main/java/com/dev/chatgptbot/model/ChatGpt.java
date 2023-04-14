package com.dev.chatgptbot.model;

import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class ChatGpt {

    private final MessageService sendMessageService;

    public ChatGpt(MessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    public String sendMessageToChatGptBot(String text) throws JsonProcessingException {
        log.debug("sendMessageToChatGpt: " + text);
        return sendMessageService.getResponse(text);

    }
}
