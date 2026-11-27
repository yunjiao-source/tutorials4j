package tutorials4j.service.ai.mcp.custom;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.support.ToolUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class DirectReturnMcpToolCallbackProvider extends SyncMcpToolCallbackProvider {

  private final List<McpSyncClient> mcpClients;

  private boolean returnDirect;

  public DirectReturnMcpToolCallbackProvider(List<McpSyncClient> mcpClients, boolean returnDirect) {
    super(mcpClients);
    this.mcpClients = mcpClients;
    this.returnDirect = returnDirect;
  }

  @Override
  public ToolCallback[] getToolCallbacks() {
    var toolCallbacks = new ArrayList<>();

    for (McpSyncClient mcpClient : mcpClients) {
      List<McpSchema.Tool> toolList = Collections.emptyList();

      try {
        toolList = mcpClient.listTools().tools();
      } catch (Exception e) {
        // 跳过该 MCP，继续处理其它的
        continue;
      }

      for (var tool : toolList) {
        toolCallbacks.add(new ReturnDirectSyncMcpToolCallback(mcpClient, tool, returnDirect));
      }
    }
    var array = toolCallbacks.toArray(new ToolCallback[0]);
    validateToolCallbacks(array);
    return array;
  }

  private void validateToolCallbacks(ToolCallback[] toolCallbacks) {
    List<String> duplicateToolNames = ToolUtils.getDuplicateToolNames(toolCallbacks);
    duplicateToolNames.forEach(s -> log.info("tool name found: {}", s));
    if (!duplicateToolNames.isEmpty()) {
      throw new IllegalStateException(
          "Multiple tools with the same name (%s)"
              .formatted(String.join(", ", duplicateToolNames)));
    }
  }
}
