package com.example.messenger.config;

import com.example.messenger.websocket.ChatWebSocketHandler;
import com.example.messenger.websocket.UsernameHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final UsernameHandshakeInterceptor usernameHandshakeInterceptor;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler, UsernameHandshakeInterceptor usernameHandshakeInterceptor){
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.usernameHandshakeInterceptor = usernameHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler((WebSocketHandler) chatWebSocketHandler, "/ws")
                .addInterceptors((HandshakeInterceptor) usernameHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173");
    }
}
