package tutorials4j.service.ai.mcp.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AiMcpServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-AI-MCP] AI MCP Service Configuration");
  }
}
