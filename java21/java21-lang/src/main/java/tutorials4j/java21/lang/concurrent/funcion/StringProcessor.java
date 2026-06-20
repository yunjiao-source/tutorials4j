package tutorials4j.java21.lang.concurrent.funcion;

/** 自定义函数式接口 */
@FunctionalInterface
public interface StringProcessor {
  String process(String input);

  // 可以有默认方法，不影响函数式接口
  default StringProcessor andThen(StringProcessor after) {
    return input -> after.process(this.process(input));
  }

  static void main(String[] args) {
    // 使用
    StringProcessor trim = String::trim;
    StringProcessor upper = String::toUpperCase;
    StringProcessor pipeline = trim.andThen(upper);

    System.out.println(pipeline.process("  hello  ")); // HELLO
  }
}
