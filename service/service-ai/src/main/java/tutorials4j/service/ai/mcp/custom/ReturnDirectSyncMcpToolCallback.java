package tutorials4j.service.ai.mcp.custom;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ReturnDirectSyncMcpToolCallback extends SyncMcpToolCallback {

  private final boolean returnDirect;

  public ReturnDirectSyncMcpToolCallback(
      McpSyncClient client, McpSchema.Tool tool, boolean returnDirect) {
    super(client, tool);
    this.returnDirect = returnDirect;
  }

  @Override
  public ToolMetadata getToolMetadata() {
    return ToolMetadata.builder().returnDirect(returnDirect).build();
  }
}
