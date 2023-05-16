package com.dev.chatgptbot.repository;

import com.dev.chatgptbot.entity.Message;
import com.dev.chatgptbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<List<Message>> findByUser(User user);

    void deleteAllByUser(User user);
}
