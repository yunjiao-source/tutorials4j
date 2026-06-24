package tutorials4j.java21.lang.demo2;

import java.util.List;
import java.util.function.Consumer;

/** 消费数据 */
public class ConsumerDemo {
  public static void main(String[] args) {
    Consumer<String> print = System.out::println;
    Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());

    print.accept("hello"); // hello

    // andThen：顺序执行多个 Consumer
    Consumer<String> both = print.andThen(printUpper);
    both.accept("world");
    // world
    // WORLD

    // 实际应用：forEach
    List<String> names = List.of("Alice", "Bob", "Charlie");
    names.forEach(name -> System.out.println("Hello, " + name));

    // 方法引用
    names.forEach(System.out::println);
  }
}
