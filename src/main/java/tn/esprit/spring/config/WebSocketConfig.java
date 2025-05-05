package tn.esprit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import tn.esprit.spring.services.implementations.VideoStreamHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VideoStreamHandler videoStreamHandler;

    public WebSocketConfig(VideoStreamHandler videoStreamHandler) {
        this.videoStreamHandler = videoStreamHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(videoStreamHandler, "/ws").setAllowedOrigins("*");
    }
}
