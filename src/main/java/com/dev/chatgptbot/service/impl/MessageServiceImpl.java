package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.config.ChatGptConfig;
import com.dev.chatgptbot.model.pojo.ChatCompletion;
import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.net.URI;
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
@Service
@Log4j
public class MessageServiceImpl implements MessageService {

    private final RestTemplate restTemplate;
    private final ChatGptConfig chatGptConfig;
    private final ObjectMapper objectMapper;
    private ChatCompletion chatCompletion;
    private List<String> messageHistory = new ArrayList<>();

    public MessageServiceImpl(ChatGptConfig chatGptConfig,
                              ObjectMapper objectMapper,
                              RestTemplate restTemplate,
                              ChatCompletion chatCompletion) {
        this.chatGptConfig = chatGptConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.chatCompletion = chatCompletion;
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

    @Override
    public String sendVoiceRequest() {
        try {
            System.out.println("sendVoiceRequest");
            HttpEntity<MultiValueMap<String, Object>> requestEntityForVoice = transcribeAudio();
            System.out.println("requestEntityForVoice " + requestEntityForVoice);
            String response = restTemplate.postForObject("https://api.openai.com/v1/audio/transcriptions",
                    requestEntityForVoice,
                    String.class);
            System.out.println("response " + response);
            objectMapper.registerModule(new JavaTimeModule());
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

    public HttpEntity<MultiValueMap<String, Object>> transcribeAudio() throws Exception {

        String fileName = "voice.wav"; // имя файла
        Class<? extends MessageServiceImpl> currentClass = getClass();
        ClassLoader classLoader = currentClass.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        System.out.println("file " + file);

        // Формирование заголовков для запроса к OpenAI API
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + chatGptConfig.getChatToken());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Формирование тела запроса с добавлением аудио файла и наименованием модели
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("file", file);
        requestBody.add("model", "whisper-1");
        System.out.println("requestBody"+ requestBody);

        // Формирование объекта HttpEntity для отправки запроса
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//        System.out.println("requestEntity " + requestEntity);

        // Отправка запроса к OpenAI API
//        ResponseEntity<String> response = restTemplate.exchange(
//                new URI("https://api.openai.com/v1/audio/transcriptions"),
//                HttpMethod.POST,
//                requestEntity,
//                String.class
//        );
//        System.out.println("response " + response);

        // Обработка ответа от OpenAI API
        return new HttpEntity<>(requestBody, headers);
    }
}