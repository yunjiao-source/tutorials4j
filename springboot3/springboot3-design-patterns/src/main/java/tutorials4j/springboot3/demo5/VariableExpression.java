package tutorials4j.springboot3.demo5;

/** 终结符表达式：变量解析器 */
public class VariableExpression implements Expression {
  // 变量名
  private final String key;

  public VariableExpression(String key) {
    this.key = key;
  }

  // 从上下文获取变量值并返回
  @Override
  public int interpret(Context context) {
    return context.get(key);
  }
}
