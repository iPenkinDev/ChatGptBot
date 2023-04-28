package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.text2text.ChatCompletion;
import com.dev.chatgptbot.service.MessageRequestService;
import com.dev.chatgptbot.util.ChatGptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@SuppressWarnings("ALL")
@Service
@Log4j
public class MessageRequestRequestServiceImpl implements MessageRequestService {

    private final RestTemplate restTemplate;
    private final ChatGptConfig chatGptConfig;
    private final ChatGptUtils chatGptUtils;
    private final ObjectMapper objectMapper;
    private ChatCompletion chatCompletion;

    @Autowired
    public MessageRequestRequestServiceImpl(ChatGptConfig chatGptConfig,
                                            ObjectMapper objectMapper,
                                            RestTemplate restTemplate,
                                            ChatGptUtils chatGptUtils, ChatCompletion chatCompletion) {
        this.chatGptConfig = chatGptConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.chatGptUtils = chatGptUtils;
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
        int retries = 3;
        while (retries > 0) {
            try {
                HttpEntity<Map<String, Object>> requestEntity = buildRequest(message);
                String response = restTemplate.postForObject(chatGptUtils.getGPT_SEND_MESSAGE_URL(), requestEntity, String.class);

                objectMapper.registerModule(new JavaTimeModule());
                chatCompletion = objectMapper.readValue(response, ChatCompletion.class);
                return chatCompletion.getChoices().get(0).getMessage().getContent();
            } catch (Exception e) {
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("Request failed after " + retries + " retries.", e);
                } else {
                    log.error("Error executing request. Trying again in 1 seconds. Retries left: " + retries);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        throw new RuntimeException("Request failed after all retries.");
    }

    private HttpEntity<Map<String, Object>> buildRequest(String message) {

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
        userMessage.put("content", message);

        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);

        return new HttpEntity<>(requestBody, headers);
    }
}