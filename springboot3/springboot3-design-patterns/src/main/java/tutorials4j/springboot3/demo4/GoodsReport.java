package tutorials4j.springboot3.demo4;

/** 商品报表实体 专属字段：上架商品数、下架商品数 */
public class GoodsReport extends BaseReport {
  // 上架商品数
  private Integer onSaleNum;
  // 下架商品数
  private Integer offSaleNum;

  @Override
  public void accept(ReportVisitor visitor) {
    visitor.visitGoodsReport(this);
  }

  public GoodsReport(Integer onSaleNum, Integer offSaleNum) {
    this.onSaleNum = onSaleNum;
    this.offSaleNum = offSaleNum;
  }

  public Integer getOnSaleNum() {
    return onSaleNum;
  }

  public Integer getOffSaleNum() {
    return offSaleNum;
  }
}
