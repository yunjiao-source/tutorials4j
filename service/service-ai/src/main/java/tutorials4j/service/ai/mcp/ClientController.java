package tutorials4j.service.ai.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {
  private final ChatFacadeCache chatFacadeCache;
  private final List<McpSyncClient> mcpSyncClients;
  private final SyncMcpToolCallbackProvider toolCallbackProvider;

  @GetMapping("/call-tool")
  public Object callTool(@RequestParam("city") String city) {
    String toolName = "getWeather";
    Map<String, Object> param = Map.of("city", city);

    for (McpSyncClient client : mcpSyncClients) {
      McpSchema.Implementation clientInfo = client.getClientInfo();
      McpSchema.Implementation serverInfo = client.getServerInfo();
      log.info("clientInfo: {}", clientInfo);
      log.info("serverInfo: {}", serverInfo);
      try {
        log.info("开始调用mcp服务");
        McpSchema.CallToolRequest request =
            McpSchema.CallToolRequest.builder().name(toolName).arguments(param).build();
        McpSchema.CallToolResult result = client.callTool(request);
        log.info("callTool result: {}", result);
        return result;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      log.info("====================================================");
    }
    return null;
  }

  @GetMapping("/chat")
  public String chat(@RequestParam("query") String query) {
    log.info("chat request => {}", query);

    return getChatFacade().client().prompt().user(query).call().content();
  }

  public ChatFacade getChatFacade() {
    ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
    // DirectReturnMcpToolCallbackProvider toolCallbacks = new
    // DirectReturnMcpToolCallbackProvider(mcpSyncClients,true);
    return chatFacadeCache.getDefault(
        "mcp-client",
        builder -> {
          builder.defaultToolCallbacks(toolCallbacks);
        });
  }
}
