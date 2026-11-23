package tutorials4j.service.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WeatherTools {

  @Tool(description = "Get the current weather for a given city")
  public String getWeather(String city) {
    Utils.greenSystemOut("getWeather 工具调用：" + city);
    return city + " 的天气是晴朗，25°C";
  }

  @Tool(description = "Get the weather for a city at a specific time")
  public String getWeather1(
      @ToolParam(description = "City name") String city,
      @ToolParam(description = "Time in ISO-8601 format", required = false) String at) {
    Utils.greenSystemOut("getWeather 工具调用：" + city);
    return "时间：" + at + ",城市：" + city + " 的天气是晴朗，-1°C";
  }
}
