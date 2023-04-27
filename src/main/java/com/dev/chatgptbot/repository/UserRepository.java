package com.dev.chatgptbot.repository;

import com.dev.chatgptbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getByTelegramId(Long id);
}
