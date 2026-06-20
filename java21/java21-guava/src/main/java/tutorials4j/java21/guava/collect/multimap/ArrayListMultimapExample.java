package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

// 最通用的实现，适合绝大多数“一个 key 对应多个 value，且 value 可有重复、需要按添加顺序访问”的场景。
public class ArrayListMultimapExample {
  public static void main(String[] args) {
    Multimap<String, String> multimap = ArrayListMultimap.create();

    // 添加元素
    multimap.put("fruit", "apple");
    multimap.put("fruit", "banana");
    multimap.put("fruit", "apple"); // 故意重复
    multimap.put("fruit", "orange");

    // 获取值（返回 List，因为底层是 ArrayList）
    System.out.println(multimap.get("fruit"));
    // 输出：[apple, banana, apple, orange] —— 保留添加顺序，且允许重复

    // 遍历所有键值对
    for (java.util.Map.Entry<String, String> entry : multimap.entries()) {
      System.out.println(entry.getKey() + " -> " + entry.getValue());
    }
  }
}
