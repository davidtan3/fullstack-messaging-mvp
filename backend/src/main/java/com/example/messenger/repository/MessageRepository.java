package com.example.messenger.repository;

import com.example.messenger.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    // A private conversation is bidirectional, so both sender-recipient directions are queried together.
    @Query("""
            SELECT m
            FROM MessageEntity m
            WHERE (m.fromUsername = :user1 AND m.toUsername = :user2)
            OR (m.fromUsername = :user2 AND m.toUsername = :user1)
            ORDER BY m.sentAt
            """)
    List<MessageEntity> findMessages(@Param("user1") String user1, @Param("user2") String user2);
}
