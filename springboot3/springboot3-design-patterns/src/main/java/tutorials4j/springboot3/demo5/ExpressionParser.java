package tutorials4j.springboot3.demo5;

/** 自定义指令解析器：解析字符串指令并执行 */
public class ExpressionParser {
  private static final String RULE_REGEX = "^[a-zA-Z]+(\\+|-)[a-zA-Z]+$";

  /**
   * 解析简单运算指令（仅支持加减、变量）
   *
   * @param exprStr 指令字符串 例：a+b、a-b
   * @param context 上下文数据
   * @return 运算结果
   */
  public static int parse(String exprStr, Context context) {
    if (!exprStr.matches(RULE_REGEX)) {
      throw new RuntimeException("指令语法错误：仅支持【变量+变量/变量-变量】格式");
    }

    // 1. 处理加法指令
    if (exprStr.contains("+")) {
      String[] split = exprStr.split("\\+");
      Expression left = new VariableExpression(split[0]);
      Expression right = new VariableExpression(split[1]);
      return new AddExpression(left, right).interpret(context);
    }
    // 2. 处理减法指令
    if (exprStr.contains("-")) {
      String[] split = exprStr.split("-");
      Expression left = new VariableExpression(split[0]);
      Expression right = new VariableExpression(split[1]);
      return new SubExpression(left, right).interpret(context);
    }
    // 3. 纯变量指令
    return new VariableExpression(exprStr).interpret(context);
  }
}
