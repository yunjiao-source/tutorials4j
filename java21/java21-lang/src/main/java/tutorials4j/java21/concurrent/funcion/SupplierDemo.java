package tutorials4j.java21.concurrent.funcion;

import java.util.Random;
import java.util.function.Supplier;

/** 提供数据 */
public class SupplierDemo {
  public static void main(String[] args) {
    // 无参数，提供值（懒加载）
    Supplier<String> greeting = () -> "Hello, World!";
    System.out.println(greeting.get()); // Hello, World!

    // 随机数供应商
    Supplier<Integer> randomInt = () -> new Random().nextInt(100);
    System.out.println(randomInt.get()); // 随机数

    // 对象工厂
    Supplier<java.util.ArrayList<String>> listFactory = java.util.ArrayList::new;
    java.util.ArrayList<String> list = listFactory.get();
    list.add("Java");
    System.out.println(list); // [Java]

    // 实际应用：Optional.orElseGet（懒加载，只在需要时才执行）
    String value = java.util.Optional.<String>empty().orElseGet(() -> "默认值（懒加载）"); // Supplier
    System.out.println(value);
  }
}
