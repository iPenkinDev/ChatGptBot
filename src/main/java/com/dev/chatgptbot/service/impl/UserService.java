package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(Messages messages) {
        User user = new User();
        user.setUserName(messages.getFrom().getUsername());
        user.setFirstName(messages.getFrom().getFirst_name());
        user.setLastName(messages.getChat().getLastName());
        user.setTelegramId(messages.getFrom().getId());
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ignored) {

            return user;
        }
        return user;
    }
}
