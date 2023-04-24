package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.VoiceToString;
import com.dev.chatgptbot.service.VoiceResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class VoiceResponseServiceImpl implements VoiceResponseService {

    private final ChatGptConfig chatGptConfig;
    private VoiceToString voiceToString;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public VoiceResponseServiceImpl(ChatGptConfig chatGptConfig,
                                    VoiceToString voiceToString,
                                    OkHttpClient client,
                                    ObjectMapper objectMapper) {
        this.chatGptConfig = chatGptConfig;
        this.voiceToString = voiceToString;
        this.client = client;
        this.objectMapper = objectMapper;
    }


    @Override
    public String voiceToString(String response) {
        response = requestVoiceToChatGpt();
        return response;
    }

    public String requestVoiceToChatGpt() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "voice.wav", RequestBody.create(MediaType.parse("audio/mpeg"), new File("D:\\Pet Project\\ChatGptBot\\voice.wav")))
                .addFormDataPart("model", "whisper-1")
                .build();

        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + chatGptConfig.getChatToken())
                .add("Content-Type", "multipart/form-data")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .headers(headers)
                .post(requestBody)
                .build();

        log.debug("request: " + request);

        return response(client, request);
    }

    private String response(OkHttpClient client, Request request) {
        try (Response response = client.newCall(request).execute()) {
                voiceToString = objectMapper.readValue(response.body().string(), VoiceToString.class);

            return voiceToString.getText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}