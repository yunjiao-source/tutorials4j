package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.Set;

// 当你需要 value 唯一，同时又要保留它们加入的顺序时，这是最佳选择。
public class LinkedHashMultimapExample {
  public static void main(String[] args) {
    Multimap<String, String> multimap = LinkedHashMultimap.create();

    multimap.put("city", "Beijing");
    multimap.put("city", "Shanghai");
    multimap.put("city", "Beijing"); // 重复，被忽略
    multimap.put("city", "Guangzhou");

    Set<String> cities = (Set<String>) multimap.get("city");
    System.out.println(cities);
    // 输出：[Beijing, Shanghai, Guangzhou] —— 唯一且保持插入顺序
  }
}
