package tutorials4j.java21.concurrent.funcion;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** 判断条件 */
public class PredicateDemo {
  public static void main(String[] args) {
    // 定义判断条件
    Predicate<String> isLong = s -> s.length() > 5;
    Predicate<String> startWithJ = s -> s.startsWith("J");

    System.out.println(isLong.test("Java")); // false
    System.out.println(isLong.test("JavaScript")); // true

    // 组合：and（&&）、or（||）、negate（!）
    Predicate<String> combined = isLong.and(startWithJ);
    System.out.println(combined.test("JavaScript")); // true（长且以J开头）
    System.out.println(combined.test("Python")); // false（不以J开头）

    Predicate<String> notLong = isLong.negate();
    System.out.println(notLong.test("Go")); // true（不长）

    // 实际应用：过滤集合
    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    Predicate<Integer> isEven = n -> n % 2 == 0;
    Predicate<Integer> greaterThan5 = n -> n > 5;

    List<Integer> result =
        numbers.stream().filter(isEven.and(greaterThan5)).collect(Collectors.toList());
    System.out.println(result); // [6, 8, 10]
  }
}
