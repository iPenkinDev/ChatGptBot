package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.exception.ResourceNotFoundException;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.repository.MessageRepository;
import com.dev.chatgptbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Message> getMessagesByUser(User user) {
        Optional<List<Message>> messageOptional = messageRepository.findByUser(user);
        return messageOptional.orElseThrow(() ->
                new ResourceNotFoundException("message ", "telegramId ", user.getTelegramId()));
    }


 }
