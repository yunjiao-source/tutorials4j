package tutorials4j.springboot3.web.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 端点
 *
 * @author yangyunjiao
 */
@Configuration
@EnableWebSocket
public class MyWebSocketConfigurer implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(myTextWebSocketHandler(), "/ws").setAllowedOrigins("*");
  }

  @Bean
  public MyTextWebSocketHandler myTextWebSocketHandler() {
    return new MyTextWebSocketHandler();
  }
}
