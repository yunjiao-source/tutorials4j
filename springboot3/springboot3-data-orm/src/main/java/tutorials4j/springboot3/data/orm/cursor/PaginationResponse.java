package tutorials4j.springboot3.data.orm.cursor;

import java.util.List;
import lombok.Data;

/**
 * 分页响应
 *
 * @author Yun Jiao
 */
@Data
public class PaginationResponse<T> {
  private List<T> data; // 数据列表
  private String nextCursor; // 下一页游标
  private boolean hasMore; // 是否有更多数据
  private long total; // 总数据量（可选）
}
