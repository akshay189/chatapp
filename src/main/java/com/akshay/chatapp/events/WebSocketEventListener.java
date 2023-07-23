package com.akshay.chatapp.events;

import com.akshay.chatapp.chat.ChatMessage;
import com.akshay.chatapp.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations simpMessageSendingOperationsTemplate;

    @EventListener()
    public void handleSessionDisconnectEventListener(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        String username = (String) stompHeaderAccessor.getSessionAttributes().get("username");
        if(username != null) {
            log.info("User has left the chat {}", username);
            var chatMessage = ChatMessage.builder()
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .content(username + " left the chat")
                    .build();

            simpMessageSendingOperationsTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
