package tutorials4j.service.ai.mcp.mdoel;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class WeatherRequest {
  @ToolParam(description = "城市")
  private String city;

  @ToolParam(description = "日期")
  private String date;

  @ToolParam(description = "区县")
  private String i;

  @ToolParam(description = "街道")
  private String s;
}
