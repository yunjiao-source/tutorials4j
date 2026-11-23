package tutorials4j.service.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DemoSearchTools {
  @Tool(description = "获取指定地点的当前天气")
  public String getWeather(@ToolParam(description = "城市名") String city) {
    Utils.greenSystemOut("getWeather 工具调用：" + city);
    return city + " 天气晴朗，气温 25°C";
  }

  @Tool(description = "获取指定地点的当前时间")
  public String getCurrentTime(@ToolParam(description = "城市名") String city) {
    Utils.greenSystemOut("getCurrentTime 工具调用：" + city);
    return java.time.LocalDateTime.now() + " (" + city + ")";
  }

  @Tool(description = "查询指定地点的可用服装店")
  public String getClothingShops(@ToolParam(description = "城市名") String city) {
    Utils.greenSystemOut("getClothingShops 工具调用：" + city);
    return city + " 的服装店：Foo, Bar, Baz";
  }

  // 一个故意无关的“噪音”工具，用于验证搜索是否只返回相关工具
  @Tool(description = "计算两个整数之和")
  public int add(int a, int b) {
    Utils.greenSystemOut("add 工具调用：");
    return a + b;
  }
}
