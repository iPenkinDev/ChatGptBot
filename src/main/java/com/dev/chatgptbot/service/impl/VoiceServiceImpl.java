package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.VoiceToString;
import com.dev.chatgptbot.service.VoiceResponseService;
import com.dev.chatgptbot.service.VoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class VoiceServiceImpl implements VoiceResponseService {

    private final ChatGptConfig chatGptConfig;
    private final Retrofit retrofit;
    private final RestTemplate restTemplate;
    private VoiceToString voiceToString;
    private final VoiceService voiceService;
    private final ObjectMapper objectMapper;

    @Autowired
    public VoiceServiceImpl(ChatGptConfig chatGptConfig, Retrofit retrofit, RestTemplate restTemplate, VoiceToString voiceToString, VoiceService voiceService, ObjectMapper objectMapper) {
        this.chatGptConfig = chatGptConfig;
        this.retrofit = retrofit;
        this.restTemplate = restTemplate;
        this.voiceToString = voiceToString;
        this.voiceService = voiceService;
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

        Call<JsonObject> call = requestVoiceToChatGpt();
        JsonObject body = call.execute().body();
        String text = body.get("text").getAsString();
        System.out.println("text " + text);
        return text;

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

    @Override
    public Call<JsonObject> requestVoiceToChatGpt() throws IOException {
        File file = new File("voice.wav");

        // Создайте объект запроса
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("audio", file.getName(), requestBody);

        return voiceService.requestAudioTranscription(chatGptConfig.getChatToken(), filePart, "whisper-1");

    }
}