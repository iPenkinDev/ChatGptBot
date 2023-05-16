package com.dev.chatgptbot.model;

import com.dev.chatgptbot.service.MessageRequestService;
import com.dev.chatgptbot.service.VoiceRequestService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j
public class ChatGpt {

    private final MessageRequestService sendMessageService;
    private final VoiceRequestService voiceService;


    public ChatGpt(MessageRequestService sendMessageService, VoiceRequestService voiceService) {
        this.sendMessageService = sendMessageService;
        this.voiceService = voiceService;
    }

    public String sendMessageToChatGptBot(String text) {
        log.info("sendMessageToChatGpt: " + text);
        return sendMessageService.sendRequest(text);
    }

    public String sendVoiceMessageToChatGptBot() {
        log.info("sendVoiceMessageToChatGpt: ");
        return voiceService.voiceNotString();
    }
}
