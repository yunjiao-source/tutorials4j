package tutorials4j.springboot3;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扩展分页响应封装
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类的equals和hashCode方法
public class PageResult<T> extends Result<List<T>> implements Serializable {
  // 总条数
  private Long total;
  // 总页数
  private Integer totalPage;
  // 当前页码
  private Integer currentPage;
  // 每页条数
  private Integer pageSize;

  // 私有构造方法
  private PageResult() {
    super();
  }

  /**
   * 分页响应构造方法
   *
   * @param code 状态码
   * @param message 提示信息
   * @param data 分页数据列表
   * @param total 总条数
   * @param totalPage 总页数
   * @param currentPage 当前页
   * @param pageSize 每页条数
   */
  private PageResult(
      Integer code,
      String message,
      List<T> data,
      Long total,
      Integer totalPage,
      Integer currentPage,
      Integer pageSize) {
    super(code, message, data);
    this.total = total;
    this.totalPage = totalPage;
    this.currentPage = currentPage;
    this.pageSize = pageSize;
  }

  /**
   * 分页成功响应（静态工厂方法）
   *
   * @param data 分页数据列表
   * @param total 总条数
   * @param currentPage 当前页
   * @param pageSize 每页条数
   */
  public static <T> PageResult<T> pageSuccess(
      List<T> data, Long total, Integer currentPage, Integer pageSize) {
    // 计算总页数：总条数 ÷ 每页条数，向上取整
    Integer totalPage = (int) Math.ceil((double) total / pageSize);
    return new PageResult<>(
        ResultCode.SUCCESS.getCode(),
        ResultCode.SUCCESS.getMessage(),
        data,
        total,
        totalPage,
        currentPage,
        pageSize);
  }

  /**
   * 分页失败响应（静态工厂方法）
   *
   * @param resultCode 状态码枚举
   */
  public static <T> PageResult<T> pageFail(ResultCode resultCode) {
    return new PageResult<>(resultCode.getCode(), resultCode.getMessage(), null, 0L, 0, 0, 0);
  }
}
