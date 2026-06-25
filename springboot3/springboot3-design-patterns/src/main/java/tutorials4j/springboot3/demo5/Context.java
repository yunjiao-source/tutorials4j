package tutorials4j.springboot3.demo5;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 上下文环境：存储指令执行的所有变量数据 */
public class Context {
  // 存储自定义变量 & 对应值
  private final Map<String, Integer> variableMap = new ConcurrentHashMap<>();

  // 存入变量
  public void put(String key, Integer value) {
    variableMap.put(key, value);
  }

  // 获取变量值

  public Integer get(String key) {
    Integer value = variableMap.get(key.trim());
    if (value == null) {
      throw new RuntimeException("指令解析失败：变量【" + key + "】未定义或未赋值");
    }
    return value;
  }
}
