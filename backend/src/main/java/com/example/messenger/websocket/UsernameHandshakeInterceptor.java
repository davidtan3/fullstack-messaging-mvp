package com.example.messenger.websocket;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Map;

@Component
public class UsernameHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler webSocketHandler, Map<String, Object> attributes){
        String username = UriComponentsBuilder.fromUri(request.getURI())
                .build().getQueryParams().getFirst("username");

        if(!StringUtils.hasText(username)){
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // Normalise usernames so "Alice" and "alice" are treated as the same user
        attributes.put("username", username.trim().toLowerCase(Locale.ROOT));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){

    }
}
