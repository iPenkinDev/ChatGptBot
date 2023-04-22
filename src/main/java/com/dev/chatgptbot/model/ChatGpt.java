package com.dev.chatgptbot.model;

import com.dev.chatgptbot.service.MessageService;
import com.dev.chatgptbot.service.VoiceResponseService;
import com.dev.chatgptbot.service.VoiceService;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;

import java.io.IOException;
import java.util.Map;

@Component
@Log4j
public class ChatGpt {

    private final MessageService sendMessageService;
    private final VoiceResponseService voiceService;


    public ChatGpt(MessageService sendMessageService, VoiceResponseService voiceService) {
        this.sendMessageService = sendMessageService;
        this.voiceService = voiceService;
    }

    public String sendMessageToChatGptBot(String text) {
        log.debug("sendMessageToChatGpt: " + text);
        return sendMessageService.sendRequest(text);
    }

    public String sendVoiceMessageToChatGptBot(String response) throws IOException {
        log.debug("sendVoiceMessageToChatGpt: " + response);
        return voiceService.voiceToString(response);
    }
}
