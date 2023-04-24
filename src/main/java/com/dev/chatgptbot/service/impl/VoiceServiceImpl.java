package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.VoiceToString;
import com.dev.chatgptbot.service.VoiceResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@Service
public class VoiceServiceImpl implements VoiceResponseService {

    private final ChatGptConfig chatGptConfig;
    private final RestTemplate restTemplate;
    private VoiceToString voiceToString;
    private final ObjectMapper objectMapper;

    @Autowired
    public VoiceServiceImpl(ChatGptConfig chatGptConfig, RestTemplate restTemplate, VoiceToString voiceToString, ObjectMapper objectMapper) {
        this.chatGptConfig = chatGptConfig;
        this.restTemplate = restTemplate;
        this.voiceToString = voiceToString;
        this.objectMapper = objectMapper;
    }

//    @Override
//    public String messageFromTranscriptionText(Call<JsonObject> call) {
//        String transcriptionText = null;
//        try {
//            Response<JsonObject> response = call.execute();
//            if (response.isSuccessful() && response.body() != null) {
//                JsonObject result = response.body();
//                transcriptionText = result.toString();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return transcriptionText;
//    }

    @Override
    public String voiceToString(String response) throws IOException {
        response = requestVoiceToChatGpt();
        System.out.println("response: " + response);

        return response;

//        try {
//            Call<JsonObject> call = ;
//            JsonObject response = call.execute().body();
//        String result = restTemplate.postForObject("https://api.openai.com/v1/audio/transcriptions", requestEntity, String.class);
//
//            voiceToString = objectMapper.readValue(result, VoiceToString.class);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("result response " + voiceToString.getText());
//        return voiceToString.getText();
    }


    public String requestVoiceToChatGpt() {
// Создание объекта клиента OkHttp
        OkHttpClient client = new OkHttpClient();

// Установка параметров запроса
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "voice.wav", RequestBody.create(MediaType.parse("audio/mpeg"), new File("D:\\Pet Project\\ChatGptBot\\voice.wav")))
                .addFormDataPart("model", "whisper-1")
                .build();
        System.out.println("requestBody: " + requestBody);
// Создание заголовков запроса
        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + chatGptConfig.getChatToken())
                .add("Content-Type", "multipart/form-data")
                .build();
        System.out.println("headers: " + headers);
// Создание запроса
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .headers(headers)
                .post(requestBody)
                .build();

        System.out.println("request: " + request);
// Отправка запроса и получение ответа
        try (Response response = client.newCall(request).execute()) {

            // Десериализация ответа сервера в объект TranscriptionResponse
            ObjectMapper objectMapper = new ObjectMapper();
            voiceToString = objectMapper.readValue(response.body().string(), VoiceToString.class);

            return voiceToString.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}