package tutorials4j.springboot3.demo5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    // 1. 创建上下文环境
    Context context = new Context();
    // 2. 自定义变量赋值（模拟业务配置规则）
    context.put("a", 100);
    context.put("b", 20);
    context.put("c", 30);
    // 3. 解析执行自定义指令
    int addResult = ExpressionParser.parse("a+b", context);
    int subResult = ExpressionParser.parse("a-c", context);
    // 4. 输出结果
    System.out.println("a+b 运算结果：" + addResult); // 120
    System.out.println("a-c 运算结果：" + subResult); // 70
    System.out.println("a - c 运算结果：" + ExpressionParser.parse("a - c", context)); // 70
    System.out.println("a-c+b 运算结果：" + ExpressionParser.parse("a-c+b", context));
  }
}
