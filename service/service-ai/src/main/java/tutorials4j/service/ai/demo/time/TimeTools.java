package tutorials4j.service.ai.demo.time;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import tutorials4j.service.ai.demo.time.TimeService.Request;
import tutorials4j.service.ai.demo.time.TimeService.Response;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TimeTools {
  private final TimeService timeService;

  @Tool(description = "Get time by zone id")
  public Response getTimeByZoneId(
      @ToolParam(description = "Time zone id, such as Asia/Shanghai") String zoneId) {
    Utils.greenSystemOut("getTimeByZoneId 工具调用：" + zoneId);
    return timeService.getTimeByZoneId(new Request(zoneId));
  }
}
