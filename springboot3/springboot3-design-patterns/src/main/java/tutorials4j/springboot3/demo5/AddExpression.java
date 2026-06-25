package tutorials4j.springboot3.demo5;

/** 非终结符表达式：加法运算解析器 */
public class AddExpression implements Expression {
  // 加法左右两个表达式（支持嵌套复杂运算）
  private final Expression left;
  private final Expression right;

  public AddExpression(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  // 执行加法运算
  @Override
  public int interpret(Context context) {
    return left.interpret(context) + right.interpret(context);
  }
}
