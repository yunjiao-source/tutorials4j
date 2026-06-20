package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.util.SortedSet;

// TreeMultimap 内部使用 TreeMap 和 TreeSet，因此 键和值都会按照自然顺序（或你指定的 Comparator）排序，且 值不允许重复。
public class TreeMultimapExample {
  public static void main(String[] args) {
    // 默认使用自然排序（键按字母升序，值按数字升序）
    Multimap<String, Integer> multimap = TreeMultimap.create();

    multimap.put("banana", 3);
    multimap.put("apple", 2);
    multimap.put("apple", 1);
    multimap.put("apple", 2); // 重复值，忽略
    multimap.put("banana", 5);

    // 获取值（返回 SortedSet，因为 TreeSet 是有序的）
    SortedSet<Integer> appleValues = (SortedSet<Integer>) multimap.get("apple");
    System.out.println(appleValues);
    // 输出：[1, 2] —— 数字升序，且去重

    // 遍历所有键（自动按字母排序）
    for (String key : multimap.keySet()) {
      System.out.println(key + " -> " + multimap.get(key));
    }
    // 输出：
    // apple -> [1, 2]
    // banana -> [3, 5]
  }
}
