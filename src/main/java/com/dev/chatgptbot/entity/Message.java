package com.dev.chatgptbot.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "telegram_id")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "date_message")
    private LocalDateTime date;
}