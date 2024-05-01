package org.example.teamspark.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${socket.host}")
    private String host;

    @Value("${group.call.socket.port}")
    private int groupCallPort;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/textMessagingWebsocket");
        registry.addEndpoint("/notificationWebsocket");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/textMessagingChannel/", "/userNotification/");
//        config.enableSimpleBroker("/userNotification/");
        config.setApplicationDestinationPrefixes("/websocket");
    }

    @Bean
    public SocketIOServer groupVideoCallSocketIOServer() {
        com.corundumstudio.socketio.Configuration config =
                new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(groupCallPort);
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        config.setOrigin("*");
        config.setContext("/videoCallWebsocket");
//        config.setOrigin("http://localhost:8080");
        return new SocketIOServer(config);
    }
}