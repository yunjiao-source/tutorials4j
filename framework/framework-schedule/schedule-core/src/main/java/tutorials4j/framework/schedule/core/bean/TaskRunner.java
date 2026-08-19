package tutorials4j.framework.schedule.core.bean;

import java.util.Map;

/**
 * 任务执行器接口。
 *
 * <p>定义任务被调度触发时的执行入口，接收一组字符串参数（键值对），由实现类完成具体的业务逻辑。 该接口为函数式接口，可使用 Lambda 表达式实现。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskRunner {
  void run(Map<String, String> params);
}
