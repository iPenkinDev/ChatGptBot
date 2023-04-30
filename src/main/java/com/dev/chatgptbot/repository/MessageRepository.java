package com.dev.chatgptbot.repository;

import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message getMessageByUserOrderByDateDesc(User user);
}
