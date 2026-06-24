package tutorials4j.springboot3.demo4;

/** 报表统计访问者接口 核心：为每一种报表定义专属统计方法 */
public interface ReportVisitor {
  // 统计用户报表数据
  void visitUserReport(UserReport userReport);

  // 统计订单报表数据
  void visitOrderReport(OrderReport orderReport);

  // 统计商品报表数据
  void visitGoodsReport(GoodsReport goodsReport);
}
