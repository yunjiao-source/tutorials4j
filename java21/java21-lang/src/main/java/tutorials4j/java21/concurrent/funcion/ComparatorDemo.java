package tutorials4j.java21.concurrent.funcion;

import java.util.*;

/** 排序比较 */
public class ComparatorDemo {
  record Person(String name, int age, double salary) {}

  public static void main(String[] args) {
    List<Person> people =
        new ArrayList<>(
            List.of(
                new Person("Charlie", 30, 15000),
                new Person("Alice", 25, 20000),
                new Person("Bob", 25, 18000)));

    // 按年龄升序
    people.sort(Comparator.comparingInt(Person::age));
    people.forEach(p -> System.out.println(p.name() + " " + p.age()));

    // 按年龄升序，年龄相同按薪资降序
    people.sort(
        Comparator.comparingInt(Person::age).thenComparingDouble(Person::salary).reversed()
        // 或分开写：
        );

    // 更清晰的写法
    people.sort(
        Comparator.comparingInt(Person::age)
            .thenComparing(Comparator.comparingDouble(Person::salary).reversed()));

    people.forEach(p -> System.out.printf("%s 年龄=%d 薪资=%.0f%n", p.name(), p.age(), p.salary()));
  }
}
