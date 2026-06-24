package tutorials4j.springboot3.demo4;

/** 访问者实现类1：报表全量数据总数统计 */
public class TotalCountVisitor implements ReportVisitor {
  // 全局总数据量
  private Integer total = 0;

  // 统计用户报表总数据（注册+活跃）
  @Override
  public void visitUserReport(UserReport userReport) {
    total += userReport.getRegisterNum() + userReport.getActiveNum();
  }

  // 统计订单报表总数据（全部订单）
  @Override
  public void visitOrderReport(OrderReport orderReport) {
    total += orderReport.getOrderTotal();
  }

  // 统计商品报表总数据（上架+下架）
  @Override
  public void visitGoodsReport(GoodsReport goodsReport) {
    total += goodsReport.getOnSaleNum() + goodsReport.getOffSaleNum();
  }

  // 获取最终统计结果
  public Integer getTotal() {
    return total;
  }
}
