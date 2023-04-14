package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.ChatCompletion;
import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private final WebClient chatGptWebClient;
    private final RestTemplate restTemplate;
    private final ChatGptConfig chatGptConfig;
    private final ObjectMapper objectMapper;
    private ChatCompletion chatCompletion;

    public MessageServiceImpl(WebClient chatGptWebClient, RestTemplate restTemplate, ChatGptConfig chatGptConfig, ObjectMapper objectMapper) {
        this.chatGptWebClient = chatGptWebClient;
        this.restTemplate = restTemplate;
        this.chatGptConfig = chatGptConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public SendMessage sendMessage(Message message) {
        String result = "Hello, " + message.getFrom().getFirstName() + "!";
        System.out.println(result);

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(result)
                .build();
    }


    @Override
    public String getResponse(String message) throws JsonProcessingException {

        HttpEntity<Map<String, Object>> requestEntity = buildRequest(message);
        String response = restTemplate.postForObject(chatGptConfig.getChatUrl(), requestEntity, String.class);

        objectMapper.registerModule(new JavaTimeModule());
        chatCompletion = objectMapper.readValue(response, ChatCompletion.class);

        return chatCompletion.getChoices().get(0).getMessage().getContent();
    }

    private HttpEntity<Map<String, Object>> buildRequest(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + chatGptConfig.getChatToken());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);

        return new HttpEntity<>(requestBody, headers);
    }

}
