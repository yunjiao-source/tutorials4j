package tutorials4j.springboot3.demo4;

/** 用户报表实体 专属字段：注册人数、活跃人数 */
public class UserReport extends BaseReport {
  // 注册用户总数
  private Integer registerNum;
  // 活跃用户总数
  private Integer activeNum;

  // 接收访问者，让访问者执行当前用户报表的统计逻辑
  @Override
  public void accept(ReportVisitor visitor) {
    visitor.visitUserReport(this);
  }

  // 构造器、getter/setter
  public UserReport(Integer registerNum, Integer activeNum) {
    this.registerNum = registerNum;
    this.activeNum = activeNum;
  }

  public Integer getRegisterNum() {
    return registerNum;
  }

  public Integer getActiveNum() {
    return activeNum;
  }
}
