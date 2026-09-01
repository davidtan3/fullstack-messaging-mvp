package com.example.messenger.websocket;

import com.example.messenger.dto.ChatMessage;
import com.example.messenger.service.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final int MAX_LENGTH = 2000;

    private final SessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(SessionRegistry sessionRegistry, ObjectMapper objectMapper){
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws IOException {
        String username = webSocketSession.getAttributes().get("username").toString();

        if(!StringUtils.hasText(username)){
            webSocketSession.close(CloseStatus.BAD_DATA);
            return;
        }

        boolean registered = sessionRegistry.register(username, webSocketSession);
        if(!registered){
            sendError(webSocketSession, null, "Username " + username + "is already connected.");

            webSocketSession.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        System.out.println("Websocket connected: " + username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws IOException {
        String sender = webSocketSession.getAttributes().get("username").toString();

        ChatMessage incoming;

        try{
            incoming = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        } catch (JacksonException e) {
            sendError(webSocketSession, null, "Invalid Json message.");

            return;
        }

        if(!"CHAT".equalsIgnoreCase(incoming.type())){
            sendError(webSocketSession, incoming.clientMessageId(), "Unsupported message type.");
            return;
        }

        if (!StringUtils.hasText(incoming.content())) {
            sendError(webSocketSession, incoming.clientMessageId(), "Message cannot be empty.");
            return;
        }

        if (incoming.content().length() > MAX_LENGTH) {
            sendError(webSocketSession, incoming.clientMessageId(), "Message exceeds maximum length.");
            return;
        }

        String recipient = incoming.to().trim().toLowerCase(Locale.ROOT);

        String clientMessageId;
        if(StringUtils.hasText(incoming.clientMessageId()))
            clientMessageId = incoming.clientMessageId();
        else
            clientMessageId = UUID.randomUUID().toString();

        ChatMessage outgoing = new ChatMessage("CHAT", sender, recipient, incoming.content().trim(),
                clientMessageId, Instant.now());

        Optional<WebSocketSession> recipientSession = sessionRegistry.find(recipient);
        if(recipientSession.isEmpty()){
            sendError(webSocketSession, clientMessageId, "User '" + recipient + "' is not online.");
            return;
        }

        sendJson(recipientSession.get(), outgoing);
        if(!recipientSession.get().getId().equals(webSocketSession.getId()))
            sendJson(webSocketSession, outgoing);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status){
        String username = webSocketSession.getAttributes().get("username").toString();

        if(username != null)
            sessionRegistry.remove(username, webSocketSession);

        System.out.println("Websocket disconnected: " + username);
    }

    private void sendError(WebSocketSession webSocketSession, String clientMessageId, String message) throws IOException {
        ChatMessage error = new ChatMessage("ERROR", null, null, message, clientMessageId, Instant.now());
        sendJson(webSocketSession, error);
    }

    private void sendJson(WebSocketSession webSocketSession, ChatMessage message) throws IOException {
        String json = objectMapper.writeValueAsString(message);

        synchronized (webSocketSession){
            if(webSocketSession.isOpen()){
                webSocketSession.sendMessage(new TextMessage(json));
            }
        }
    }
}
