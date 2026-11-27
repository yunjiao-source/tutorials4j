package tutorials4j.service.ai.mcp.tool;

import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import tutorials4j.service.ai.mcp.mdoel.Ticket;
import tutorials4j.service.ai.mcp.service.TicketService;
import tutorials4j.toolkit.core.util.SecurityUtils;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketTools {
  private final TicketService ticketService;

  // 纯函数式工具：输入 → 处理 → 返回结果，完全无会话依赖
  @McpTool(name = "create_ticket", description = "根据标题创建工单")
  public Ticket createTicket(@McpToolParam(description = "工单标题") String title) {
    // 业务逻辑：可直接返回对象，框架自动序列化
    return ticketService.create(title);
  }

  // 资源模板示例（静态资源同样支持）
  @McpResource(uri = "tickets://all", mimeType = "application/json")
  public List<ResourceContents> listTickets() {
    log.info("=== 登录用户：{} === ", SecurityUtils.getAccount());
    return ticketService.findAll().stream()
        .map(
            ticket ->
                (ResourceContents)
                    TextResourceContents.builder(
                            "tickets://" + ticket.getId(), JacksonUtils.instance.toJson(ticket))
                        .mimeType("application/json")
                        .build())
        .toList();
  }
}
