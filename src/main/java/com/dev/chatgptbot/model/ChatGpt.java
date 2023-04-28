package com.dev.chatgptbot.model;

import com.dev.chatgptbot.service.MessageRequestService;
import com.dev.chatgptbot.service.VoiceResponseService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j
public class ChatGpt {

    private final MessageRequestService sendMessageService;
    private final VoiceResponseService voiceService;


    public ChatGpt(MessageRequestService sendMessageService, VoiceResponseService voiceService) {
        this.sendMessageService = sendMessageService;
        this.voiceService = voiceService;
    }

    public String sendMessageToChatGptBot(String text) {
        log.info("sendMessageToChatGpt: " + text);
        return sendMessageService.sendRequest(text);
    }

    public String sendVoiceMessageToChatGptBot(String response) throws IOException, InterruptedException {
        log.info("sendVoiceMessageToChatGpt: " + response);
        return voiceService.voiceToString(response);
    }
}
