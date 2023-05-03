package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.repository.MessageRepository;
import com.dev.chatgptbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public void create (Messages messages, Long telegramId) {
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

}
