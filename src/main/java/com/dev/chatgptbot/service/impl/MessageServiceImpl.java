package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.ChatCompletion;
import com.dev.chatgptbot.model.pojo.VoiceToString;
import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("ALL")
@Service
@Log4j
public class MessageServiceImpl implements MessageService {

    private final RestTemplate restTemplate;
    private final ChatGptConfig chatGptConfig;
    private final ObjectMapper objectMapper;
    private ChatCompletion chatCompletion;
    private List<String> messageHistory = new ArrayList<>();

    @Autowired
    public MessageServiceImpl(ChatGptConfig chatGptConfig,
                              ObjectMapper objectMapper,
                              RestTemplate restTemplate,
                              ChatCompletion chatCompletion) {
        this.chatGptConfig = chatGptConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.chatCompletion = chatCompletion;
    }

    public SendMessage sendMessage(Message message) {
        String result = "Hello, " + message.getFrom().getFirstName() + "!";
        System.out.println(result);

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(result)
                .build();
    }

    @Override
    public String sendRequest(String message) {
        //create message history
        messageHistory.add(message);

        //send request
        HttpEntity<Map<String, Object>> requestEntity = buildRequest(messageHistory);
        String response = restTemplate.postForObject(chatGptConfig.getChatUrl(), requestEntity, String.class);

        objectMapper.registerModule(new JavaTimeModule());
        try {
            chatCompletion = objectMapper.readValue(response, ChatCompletion.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chatCompletion.getChoices().get(0).getMessage().getContent();

    }



    private HttpEntity<Map<String, Object>> buildRequest(List<String> messageHistory) {

        //create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + chatGptConfig.getChatToken());

        //create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", messageHistory.get(messageHistory.size() - 1));
        log.debug("message history: " + messageHistory);

        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);

        return new HttpEntity<>(requestBody, headers);
    }
}