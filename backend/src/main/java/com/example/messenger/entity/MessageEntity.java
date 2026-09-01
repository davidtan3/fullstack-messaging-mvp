package com.example.messenger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "messages")
public class MessageEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "from_username", nullable = false)
    private String fromUsername;

    @Column(name = "to_username", nullable = false)
    private String toUsername;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "client_message_id")
    private String clientMessageId;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    public MessageEntity(
            String fromUsername,
            String toUsername,
            String content,
            String clientMessageId,
            Instant sentAt) {
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.content = content;
        this.clientMessageId = clientMessageId;
        this.sentAt = sentAt;
    }
}