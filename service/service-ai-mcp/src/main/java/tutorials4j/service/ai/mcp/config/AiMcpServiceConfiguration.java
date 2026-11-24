package tutorials4j.service.ai.mcp.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.service.ai.mcp.service.WeatherService;

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

  @Bean
  public ToolCallbackProvider weatherTools(WeatherService weatherService) {
    return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
  }
}
