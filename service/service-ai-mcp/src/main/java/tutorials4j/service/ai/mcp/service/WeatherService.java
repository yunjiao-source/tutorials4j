package tutorials4j.service.ai.mcp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import tutorials4j.service.ai.mcp.mdoel.WeatherRequest;
import tutorials4j.service.ai.mcp.mdoel.WeatherResponse;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class WeatherService {
  @Tool(name = "getWeather", description = "根据城市名称查询天气信息")
  public String getWeather(String city) {
    if (city == null) {
      return "请提供城市名称";
    }
    return switch (city) {
      case "北京" -> "北京: 晴, 25°C";
      case "上海" -> "上海: 多云, 22°C";
      case "深圳" -> "深圳: 小雨, 28°C";
      default -> city + ": 下雪, -20°C";
    };
  }

  @Tool(name = "query_weather_by_city_date", description = "根据城市和日期获取天气信息")
  public WeatherResponse queryWeather(WeatherRequest request) {
    log.info("query weather for city:{}", request);
    try {
      // 模拟调用api
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    double temp = Math.random() * 15 + 10;

    return new WeatherResponse(
        request.getCity(), request.getDate(), request.getI(), request.getS(), "晴朗，有微风", temp);
  }
}
