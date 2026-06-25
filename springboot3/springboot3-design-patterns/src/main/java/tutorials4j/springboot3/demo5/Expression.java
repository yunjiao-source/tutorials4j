package tutorials4j.springboot3.demo5;

/** 抽象解释器接口：所有指令/表达式的顶层规范 */
public interface Expression {
  /**
   * 解释执行方法
   *
   * @param context 上下文环境（存储变量、参数）
   * @return 执行结果
   */
  int interpret(Context context);
}
