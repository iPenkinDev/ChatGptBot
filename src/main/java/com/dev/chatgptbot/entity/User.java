package com.dev.chatgptbot.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Component
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable{

    @Id
    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

    public Long getTelegramId() {
        return telegramId;
    }
}