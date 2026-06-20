package tutorials4j.java21.guava.collect.multimap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Set;

// 当你需要保证一个 key 下的所有 value 互不相同，并且不关心顺序时使用。
public class HashMultimapExample {
  public static void main(String[] args) {
    Multimap<String, Integer> multimap = HashMultimap.create();

    multimap.put("id", 1001);
    multimap.put("id", 1002);
    multimap.put("id", 1001); // 重复的 value，不会加入
    multimap.put("id", 1003);

    // 获取值（返回 Set，因为底层是 HashSet）
    Set<Integer> ids = (Set<Integer>) multimap.get("id");
    System.out.println(ids);
    // 输出可能是 [1001, 1002, 1003]（顺序不固定，取决于哈希）

    // 验证唯一性
    System.out.println("Size: " + ids.size()); // 3，而不是 4
  }
}
