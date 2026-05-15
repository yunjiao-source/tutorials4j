package tutorials4j.framework.data.core.jpa;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * 通用分页返回对象，替代 PageImpl 序列化
 *
 * @author Yun Jiao
 */
@Data
public class PageResult<T> {
  // 数据列表
  private List<T> content;
  // 当前页码（从0开始/从1开始可自定义）
  private int pageNumber;
  // 每页条数
  private int pageSize;
  // 总页数
  private int totalPages;
  // 总记录数
  private long totalElements;
  // 是否最后一页
  private boolean last;

  // 构造方法：从 Spring Page 对象转换
  public PageResult(Page<T> page) {
    this.content = page.getContent();
    this.pageNumber = page.getNumber() + 1; // 前端习惯从1开始页码
    this.pageSize = page.getSize();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.last = page.isLast();
  }

  public static <T> PageResult<T> of(Page<T> page) {
    return new PageResult<>(page);
  }
}
