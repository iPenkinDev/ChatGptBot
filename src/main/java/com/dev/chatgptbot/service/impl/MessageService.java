package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.repository.MessageRepository;
import com.dev.chatgptbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public void create(Messages messages, Long telegramId) {
        Message message = new Message();
        message.setMessage(messages.getText());
        message.setDate(LocalDateTime.now());
        message.setUser(userRepository.getByTelegramId(telegramId));

        messageRepository.save(message);
    }

    public void createFromVoice(String textFromVoice, Long telegramId) {
        Message message = new Message();
        message.setMessage(textFromVoice);
        message.setDate(LocalDateTime.now());
        message.setUser(userRepository.getByTelegramId(telegramId));

        messageRepository.save(message);
    }

    public List<String> getMessageByUserTelegramIdOrderByDateDesc(Long telegramId) {
        List<String> messagesList = new ArrayList<>();
        try {
            Long userId = userRepository.getByTelegramId(telegramId).getTelegramId();
            Message messageByUserTelegramIdOrderByDateDesc = messageRepository.getMessageByUserTelegramIdOrderByDateDesc(userId);
            for (int i = 0; i < 5; i++) {
                messagesList.add(messageByUserTelegramIdOrderByDateDesc.getMessage());
            }
            return messagesList;
        } catch (RuntimeException e) {
            log.error("User not found: " + e.getMessage());
        }
        return null;
    }
}
