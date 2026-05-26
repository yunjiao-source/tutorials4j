package tutorials4j.springboot3.batch.simple.user;

import lombok.Data;

/**
 * 输入数据模型（CSV 行）
 *
 * @author Yun Jiao
 */
@Data
public class UserCsvRecord {
  private String name;
  private String email;

  // 必须有无参构造
  public UserCsvRecord() {}

  public UserCsvRecord(String name, String email) {
    this.name = name;
    this.email = email;
  }
}
