package tutorials4j.springboot3.data.orm.cursor;

import lombok.Data;

/**
 * 分页请求
 *
 * @author Yun Jiao
 */
@Data
public class PaginationRequest {
  private Integer pageSize = 30; // 默认每页 10 条
  private String cursor; // 游标，格式为 "id:timestamp"
  private String sortBy = "createdAt"; // 默认按创建时间排序
  private String sortDirection = "desc"; // 默认降序
}
