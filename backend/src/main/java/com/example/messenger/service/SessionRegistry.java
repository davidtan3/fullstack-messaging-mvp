package com.example.messenger.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public boolean register(String username, WebSocketSession webSocketSession){
        WebSocketSession existingSession = sessions.putIfAbsent(username, webSocketSession);

        if(existingSession==null)
            return true;

        if(!existingSession.isOpen())
            return sessions.replace(username, existingSession, (WebSocketSession) sessions);

        return false;
    }

    public Optional<WebSocketSession> find(String username){
        WebSocketSession webSocketSession = sessions.get(username);

        if(webSocketSession == null || !webSocketSession.isOpen())
            return Optional.empty();

        return Optional.of(webSocketSession);
    }

    public void remove(String username, WebSocketSession webSocketSession){
        sessions.remove(username, webSocketSession);
    }

}
