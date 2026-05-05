package dev.aja.aja.forumstatus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración del websocket, especificando registerStompEndPoint para
 * realizar handshake (conexión), el broker donde se enviará la información
 * actualizada para quel os usuarios puedan verla y especificamos prefijo para
 * todos las annotation MessageMapping y que comiencen por /api/<MessageMapping>
 */
@Configuration
@EnableWebSocketMessageBroker
public class ForumStatusConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws-connection").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/status");
        config.setApplicationDestinationPrefixes("/api");
    }

}