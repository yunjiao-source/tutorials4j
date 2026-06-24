package tutorials4j.springboot3.demo4;

/** 报表数据抽象父类（所有报表的顶层父类） 核心：预留接收访问者的入口方法 */
public abstract class BaseReport {
  /**
   * 接收访问者（统计器）的抽象方法
   *
   * @param visitor 报表统计访问者
   */
  public abstract void accept(ReportVisitor visitor);
}
