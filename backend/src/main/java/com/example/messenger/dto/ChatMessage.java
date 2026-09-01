package com.example.messenger.dto;

import java.time.Instant;

public record ChatMessage(
        String type,
        String from,
        String to,
        String content,
        String clientMessageId,
        Instant sentAt
) {

}
