package tutorials4j.springboot3.demo4;

/** 订单报表实体 专属字段：订单总数、失效订单数 */
public class OrderReport extends BaseReport {
  // 订单总数
  private Integer orderTotal;
  // 失效订单数
  private Integer invalidOrderNum;

  @Override
  public void accept(ReportVisitor visitor) {
    visitor.visitOrderReport(this);
  }

  public OrderReport(Integer orderTotal, Integer invalidOrderNum) {
    this.orderTotal = orderTotal;
    this.invalidOrderNum = invalidOrderNum;
  }

  public Integer getOrderTotal() {
    return orderTotal;
  }

  public Integer getInvalidOrderNum() {
    return invalidOrderNum;
  }
}
