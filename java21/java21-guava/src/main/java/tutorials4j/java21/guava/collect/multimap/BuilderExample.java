package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

// 如果上述实现无法完全满足需求，你还可以使用 MultimapBuilder 来组合任意类型的 Map 和 Collection，例如允许重复、
// 但使用 LinkedHashSet 来去重？（其实不常见，但 builder 可以做到）
public class BuilderExample {
  public static void main(String[] args) {
    // 创建一个 key 为 String，value 集合为 LinkedHashSet 的 Multimap（保持顺序且去重）
    Multimap<String, Integer> multimap =
        MultimapBuilder.hashKeys() // 使用 HashMap 作为底层 Map
            .linkedHashSetValues() // 每个 key 对应一个 LinkedHashSet
            .build();

    multimap.put("a", 1);
    multimap.put("a", 2);
    multimap.put("a", 1); // 重复，被忽略
    System.out.println(multimap.get("a")); // [1, 2]（保持插入顺序）
  }
}
