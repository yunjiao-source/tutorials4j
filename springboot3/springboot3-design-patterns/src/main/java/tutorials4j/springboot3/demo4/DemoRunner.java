package tutorials4j.springboot3.demo4;

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
    // 1. 构建报表数据容器，添加各类报表数据
    ReportDataContainer container = new ReportDataContainer();
    container.addReport(new UserReport(1000, 800)); // 用户报表：注册1000，活跃800
    container.addReport(new OrderReport(500, 50)); // 订单报表：总订单500，失效50
    container.addReport(new GoodsReport(200, 30)); // 商品报表：上架200，下架30
    // 2. 执行全量数据统计
    TotalCountVisitor totalVisitor = new TotalCountVisitor();
    container.acceptVisitor(totalVisitor);
    System.out.println("报表全量数据总数：" + totalVisitor.getTotal());
    // 3. 执行异常数据统计
    ErrorDataCountVisitor errorVisitor = new ErrorDataCountVisitor();
    container.acceptVisitor(errorVisitor);
    System.out.println("报表异常/失效数据总数：" + errorVisitor.getErrorTotal());
  }
}
