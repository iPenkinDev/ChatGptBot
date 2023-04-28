package com.dev.chatgptbot.service.impl;

import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j
public class UserService {

    private final UserRepository userRepository;

    public User create(Messages messages) {
        User user = new User();
        user.setUserName(messages.getFrom().getUsername());
        user.setFirstName(messages.getFrom().getFirst_name());
        user.setLastName(messages.getChat().getLastName());
        user.setTelegramId(messages.getFrom().getId());

        userRepository.save(user);
        return user;
    }

    public User getByTelegramId(Long telegramId) {
        try {
            return userRepository.getByTelegramId(telegramId);
        } catch (RuntimeException e){
            log.debug("Resource Not Found Exception");
        }
        return null;
    }
}
