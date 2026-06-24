package tutorials4j.springboot3.demo4;

/** 访问者实现类2：报表异常/失效数据统计 */
public class ErrorDataCountVisitor implements ReportVisitor {
  // 异常数据总数
  private Integer errorTotal = 0;

  // 用户报表无异常数据，无需统计
  @Override
  public void visitUserReport(UserReport userReport) {}

  // 统计失效订单（异常订单数据）
  @Override
  public void visitOrderReport(OrderReport orderReport) {
    errorTotal += orderReport.getInvalidOrderNum();
  }

  // 统计下架商品（异常/失效商品数据）
  @Override
  public void visitGoodsReport(GoodsReport goodsReport) {
    errorTotal += goodsReport.getOffSaleNum();
  }

  public Integer getErrorTotal() {
    return errorTotal;
  }
}
