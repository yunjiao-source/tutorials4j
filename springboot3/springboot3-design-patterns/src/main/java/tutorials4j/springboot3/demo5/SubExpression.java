package tutorials4j.springboot3.demo5;

/** 非终结符表达式：减法运算解析器 */
public class SubExpression implements Expression {
  private final Expression left;
  private final Expression right;

  public SubExpression(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public int interpret(Context context) {
    return left.interpret(context) - right.interpret(context);
  }
}
