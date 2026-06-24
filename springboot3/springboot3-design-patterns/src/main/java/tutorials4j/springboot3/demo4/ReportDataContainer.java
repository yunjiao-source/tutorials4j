package tutorials4j.springboot3.demo4;

import java.util.ArrayList;
import java.util.List;

/** 报表数据容器：统一管理所有报表数据，批量执行统计 */
public class ReportDataContainer {
  // 存储所有类型的报表数据
  private List<BaseReport> reportList = new ArrayList<>();

  // 添加报表数据
  public void addReport(BaseReport report) {
    reportList.add(report);
  }

  // 批量接收访问者，执行统计逻辑
  public void acceptVisitor(ReportVisitor visitor) {
    for (BaseReport report : reportList) {
      report.accept(visitor);
    }
  }
}
