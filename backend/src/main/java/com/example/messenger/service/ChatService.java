package com.example.messenger.service;

import com.example.messenger.dto.ChatMessage;
import com.example.messenger.entity.MessageEntity;
import com.example.messenger.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatService {
    private final MessageRepository messageRepository;

    public void saveMessage(ChatMessage chatMessage){
        MessageEntity entity = new MessageEntity(
                chatMessage.from(),
                chatMessage.to(),
                chatMessage.content(),
                chatMessage.clientMessageId(),
                chatMessage.sentAt()
        );

        messageRepository.save(entity);
    }
}
