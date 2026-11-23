package tutorials4j.service.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WeatherTools1 {
  @Tool(description = "根据城市名称查询该城市的实时天气状况")
  public String getWeatherByName(String cityName) {
    Utils.greenSystemOut("工具调用：" + cityName);
    // 生产环境中，这里应调用真实的第三方天气 API。
    // 此处为模拟数据，用于演示工具执行流程。
    if ("北京".equals(cityName)) {
      return "北京当前天气：晴，气温 25°C，湿度 40%";
    } else if ("上海".equals(cityName)) {
      return "上海当前天气：多云，气温 28°C，湿度 65%";
    }
    return "暂不支持查询该城市：" + cityName;
  }
}
