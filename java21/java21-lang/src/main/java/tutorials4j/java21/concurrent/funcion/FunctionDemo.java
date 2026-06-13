package tutorials4j.java21.concurrent.funcion;

import java.util.function.Function;

/** 数据转换 */
public class FunctionDemo {
  public static void main(String[] args) {
    // 字符串转整数
    Function<String, Integer> parseInt = Integer::parseInt;
    System.out.println(parseInt.apply("42")); // 42

    // 对象转字符串
    Function<Integer, String> intToStr = n -> "数字：" + n;
    System.out.println(intToStr.apply(100)); // 数字：100

    // andThen：链式组合（先 f，再 g）
    Function<String, String> trim = String::trim;
    Function<String, String> upper = String::toUpperCase;
    Function<String, String> pipeline = trim.andThen(upper);

    System.out.println(pipeline.apply("  hello world  ")); // HELLO WORLD

    // compose：逆向组合（先 g，再 f）
    Function<Integer, Integer> times2 = x -> x * 2;
    Function<Integer, Integer> plus3 = x -> x + 3;
    // compose: 先 plus3，再 times2
    System.out.println(times2.compose(plus3).apply(5)); // (5+3)*2 = 16
    // andThen: 先 times2，再 plus3
    System.out.println(times2.andThen(plus3).apply(5)); // 5*2+3 = 13
  }
}
