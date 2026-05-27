根据提供的四个Java文件，这是一个基于Spring Boot 3的WebSocket示例项目，演示了两种不同的WebSocket实现方式：**原生WebSocket**和**STOMP协议**。下面逐一分析每个类的功能及整体协作关系。

---

### 1. `MyTextWebSocketHandler.java`（原生WebSocket处理器）

**功能**：继承`TextWebSocketHandler`，实现了原生WebSocket的核心业务逻辑。

- **连接管理**：使用`ConcurrentHashMap`保存所有活动会话（`WebSocketSession`），键为会话ID。
- **连接建立**（`afterConnectionEstablished`）：会话加入Map，打印连接数，并向新客户端发送欢迎消息。
- **文本消息处理**（`handleTextMessage`）：
    - 如果消息以`@`开头，调用`handlePrivateMessage`进行私聊处理（格式：`@目标会话ID:消息内容`）。
    - 否则调用`broadcastMessage`进行广播，广播时会在消息前加上发送者ID前缀（截取前8位），并发送给**所有**连接（包括自己，代码中注释掉了排除发送者的逻辑）。
- **连接关闭**（`afterConnectionClosed`）：从Map中移除会话，打印状态。
- **错误处理**（`handleTransportError`）：打印异常堆栈。
- **辅助方法**：
    - `sendMessage`：安全发送文本消息（检查会话是否打开）。
    - `broadcastMessage`：遍历所有会话发送格式化广播消息。
    - `handlePrivateMessage`：解析私聊指令，向目标会话发送私聊消息。
    - `getConnectionCount`：返回当前连接数（静态方法）。

**特点**：轻量级、自定义协议（通过`@`区分私聊），适合简单场景或学习原生WebSocket API。

---

### 2. `MyWebSocketConfigurer.java`（原生WebSocket配置）

**功能**：实现`WebSocketConfigurer`，注册原生WebSocket端点。

- 使用`@EnableWebSocket`启用WebSocket支持。
- 在`registerWebSocketHandlers`中，将`MyTextWebSocketHandler`映射到路径`/ws`。
- 设置`setAllowedOrigins("*")`允许跨域访问。
- 通过`@Bean`方法提供`MyTextWebSocketHandler`实例。

**作用**：使客户端能通过`ws://host:port/ws`建立原生WebSocket连接。

---

### 3. `MyWebSocketMessageBrokerConfigurer.java`（STOMP消息代理配置）

**功能**：实现`WebSocketMessageBrokerConfigurer`，配置基于STOMP协议的WebSocket消息代理。

- **启用STOMP**：`@EnableWebSocketMessageBroker`启用消息代理功能。
- **配置消息代理**（`configureMessageBroker`）：
    - `enableSimpleBroker("/topic")`：创建简单内存消息代理，客户端可订阅`/topic`开头的目的地。
    - `setApplicationDestinationPrefixes("/app")`：客户端发送消息到`/app/**`的端点时，会路由到`@MessageMapping`标注的方法。
- **注册STOMP端点**（`registerStompEndpoints`）：
    - 添加端点`/stomp-ws`，并启用SockJS回退（`withSockJS()`），以便在不支持WebSocket的浏览器中使用降级方案。

**作用**：提供标准的STOMP over WebSocket服务，客户端通过`/stomp-ws`连接，并遵循STOMP协议进行消息收发。

---

### 4. `GreetingController.java`（STOMP消息处理控制器）

**功能**：一个简单的Spring MVC风格的控制器，处理STOMP消息。

- `@Controller`声明为Spring组件。
- `@MessageMapping("/hello")`：当客户端发送消息到`/app/hello`（因为配置了前缀`/app`）时，此方法被调用。
- 参数`message`接收消息体（简单字符串）。
- `@SendTo("/topic/greetings")`：方法的返回值将作为消息发送到`/topic/greetings`主题，所有订阅该主题的客户端都会收到。
- 方法返回拼接后的字符串：`"Hello, " + message + "!"`。

**作用**：演示了如何使用注解快速实现请求-响应模式的广播通信。

---

## 整体功能总结

该项目同时提供了**两种WebSocket通信方式**：

| 方式           | 端点路径         | 协议/功能                                                   |
| -------------- | ---------------- | ----------------------------------------------------------- |
| 原生WebSocket  | `/ws`            | 自定义文本协议：广播消息、私聊（`@目标ID:内容`）、连接管理  |
| STOMP over WS  | `/stomp-ws` (SockJS) | 标准STOMP协议：客户端通过`/app/hello`发送消息，服务器广播到`/topic/greetings` |

**适用场景**：
- 原生方式适合学习原理、实现轻量级自定义即时通讯（如聊天室）。
- STOMP方式适合与现有消息代理（如RabbitMQ）集成，或需要更高级的消息路由模式（如点对点、用户队列等）。

这两个示例相互独立，可同时运行在同一Spring Boot应用中，客户端根据需要选择连接不同的端点。