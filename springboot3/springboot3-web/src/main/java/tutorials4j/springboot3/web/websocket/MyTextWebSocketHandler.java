package tutorials4j.springboot3.web.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 自定义 TextWebSocketHandler 实现
 *
 * @author yangyunjiao
 */
public class MyTextWebSocketHandler extends TextWebSocketHandler {

  // 保存所有连接的会话
  private static final ConcurrentHashMap<String, WebSocketSession> sessions =
      new ConcurrentHashMap<>();

  /** 连接建立后触发 */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    String sessionId = session.getId();
    sessions.put(sessionId, session);

    System.out.println("新连接建立: " + sessionId);
    System.out.println("当前连接数: " + sessions.size());

    // 发送欢迎消息
    sendMessage(session, "欢迎连接 WebSocket 服务器！");
  }

  /** 处理文本消息 */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    String payload = message.getPayload();
    String sessionId = session.getId();

    System.out.println("收到消息 [" + sessionId + "]: " + payload);

    // 处理不同类型的消息
    if (payload.startsWith("@")) {
      handlePrivateMessage(sessionId, payload);
    } else {
      broadcastMessage(sessionId, payload);
    }
  }

  /** 连接关闭后触发 */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String sessionId = session.getId();
    sessions.remove(sessionId);

    System.out.println("连接关闭: " + sessionId);
    System.out.println("关闭原因: " + status);
    System.out.println("剩余连接数: " + sessions.size());
  }

  /** 处理传输错误 */
  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    System.err.println("传输错误: " + session.getId());
    exception.printStackTrace();
  }

  // 发送消息给指定会话
  private void sendMessage(WebSocketSession session, String message) {
    try {
      if (session.isOpen()) {
        session.sendMessage(new TextMessage(message));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 广播消息给所有连接
  private void broadcastMessage(String senderId, String message) {
    String formattedMessage = String.format("用户[%s]: %s", senderId.substring(0, 8), message);

    sessions.forEach(
        (id, session) -> {
          // if (!id.equals(senderId)) { // 不发送给自己
          sendMessage(session, formattedMessage);
          // }
        });
  }

  // 处理私聊消息
  private void handlePrivateMessage(String senderId, String message) {
    // 格式: @目标会话ID:消息内容
    try {
      String[] parts = message.substring(1).split(":", 2);
      if (parts.length == 2) {
        String targetId = parts[0];
        String content = parts[1];

        WebSocketSession targetSession = sessions.get(targetId);
        if (targetSession != null && targetSession.isOpen()) {
          String privateMsg = String.format("私聊[%s]: %s", senderId.substring(0, 8), content);
          sendMessage(targetSession, privateMsg);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 获取所有连接
  public static int getConnectionCount() {
    return sessions.size();
  }
}
