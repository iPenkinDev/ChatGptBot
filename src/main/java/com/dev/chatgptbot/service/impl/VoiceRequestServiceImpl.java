package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.voice2text.VoiceToString;
import com.dev.chatgptbot.service.VoiceRequestService;
import com.dev.chatgptbot.util.ChatGptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class VoiceRequestServiceImpl implements VoiceRequestService {

    private final ChatGptConfig chatGptConfig;
    private final ChatGptUtils chatGptUtils;
    private VoiceToString voiceToString;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public VoiceRequestServiceImpl(ChatGptConfig chatGptConfig,
                                   ChatGptUtils chatGptUtils, VoiceToString voiceToString,
                                   OkHttpClient client,
                                   ObjectMapper objectMapper) {
        this.chatGptConfig = chatGptConfig;
        this.chatGptUtils = chatGptUtils;
        this.voiceToString = voiceToString;
        this.client = client;
        this.objectMapper = objectMapper;
    }


    @Override
    public String voiceToString(String response) {
        response = requestVoiceToChatGpt();
        return response;
    }

    @Override
    public String voiceNotString() {
        return requestVoiceToChatGpt();
    }

    public String requestVoiceToChatGpt() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "voice.wav", RequestBody.create(MediaType.parse("audio/mpeg"), new File("voice/voice.wav")))
                .addFormDataPart("model", "whisper-1")
                .build();

        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + chatGptConfig.getChatToken())
                .add("Content-Type", "multipart/form-data")
                .build();

        Request request = new Request.Builder()
                .url(chatGptUtils.getGPT_SEND_VOICE_URL())
                .headers(headers)
                .post(requestBody)
                .build();

        log.info("request: " + request);

        return response(client, request);
    }

    private String response(OkHttpClient client, Request request) {
        int retries = 5; // количество попыток повтора
        while (retries > 0) {
            try (Response response = client.newCall(request).execute()) {
                voiceToString = objectMapper.readValue(response.body().string(), VoiceToString.class);
                return voiceToString.getText();
            } catch (IOException e) {
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("Error executing request. Maximum number of retries exceeded.", e);
                }
                log.error("Error executing request. Trying again in 1 seconds. Retries left: " + retries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return "Error executing request";
    }
}