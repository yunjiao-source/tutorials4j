package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

// 与 ArrayListMultimap 类似，但底层是链表，适合需要频繁在值列表中间进行插入或删除操作的场景（例如维护一个操作日志序列）
public class LinkedListMultimapExample {
  public static void main(String[] args) {
    Multimap<String, Integer> multimap = LinkedListMultimap.create();

    multimap.put("score", 90);
    multimap.put("score", 85);
    multimap.put("score", 95);

    // 在索引 1 位置插入一个新值（LinkedList 的特性）
    // 注意：Multimap 接口不直接提供按索引插入的方法，但我们可以通过获取 List 来操作
    java.util.List<Integer> scores = (java.util.List<Integer>) multimap.get("score");
    scores.add(1, 88); // 在第二个位置插入

    System.out.println(multimap.get("score"));
    // 输出：[90, 88, 85, 95] —— 插入顺序保留，且允许重复
  }
}
