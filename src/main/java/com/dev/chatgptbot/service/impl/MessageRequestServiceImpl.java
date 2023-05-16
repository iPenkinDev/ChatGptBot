package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.model.pojo.text2text.ChatCompletion;
import com.dev.chatgptbot.repository.UserRepository;
import com.dev.chatgptbot.service.MessageRequestService;
import com.dev.chatgptbot.util.ChatGptUtils;
import com.dev.chatgptbot.util.UserSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Service
@Log4j
@RequiredArgsConstructor
public class MessageRequestServiceImpl implements MessageRequestService {
    private final UserRepository userRepository;

    private final RestTemplate restTemplate;
    private final ChatGptConfig chatGptConfig;
    private final ChatGptUtils chatGptUtils;
    private final ObjectMapper objectMapper;
    private ChatCompletion chatCompletion;
    private final MessageService messageService;

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
            } catch (RuntimeException | JsonProcessingException e) {
                retries--;
                if (retries == 0) {
                    log.error("Request failed after " + retries + " retries.", e);
                } else {
                    log.error("Error executing request. Trying again in 1 seconds. Retries left: " + retries);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error("InterruptedExcxeption: " + ex.getMessage());
                    }
                }
            }
        }
        throw new RuntimeException("Request failed after all retries.");
    }

    private HttpEntity<Map<String, Object>> buildRequest(String textMessage) {
        long savedUserId = UserSession.getSavedUserId();
        List<Message> messagesByUser = messageService.getMessagesByUser(userRepository.getByTelegramId(savedUserId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + chatGptConfig.getChatToken());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        List<Map<String, Object>> messages = new ArrayList<>();

        for (Message message : messagesByUser) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("role", "user");
            messageMap.put("content", message.getMessage());
            messages.add(messageMap);
           // log.info("messageMap = " + messageMap);
        }

        requestBody.put("messages", messages);

        return new HttpEntity<>(requestBody, headers);
    }
    // double cost = totalToken * 0,000002;
}