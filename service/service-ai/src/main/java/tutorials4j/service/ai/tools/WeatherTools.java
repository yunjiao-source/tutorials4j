package tutorials4j.service.ai.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import tutorials4j.service.ai.service.WeatherService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherTools {
  private final WeatherService weatherService;

  @Tool(description = "Get the current weather for a given city")
  public String getWeather(String city) {
    return weatherService.fetch(city);
  }

  @Tool(description = "Get the weather for a city at a specific time")
  public String getWeather(
      @ToolParam(description = "City name") String city,
      @ToolParam(description = "Time in ISO-8601 format", required = false) String at) {
    return weatherService.fetch(city, at);
  }
}
