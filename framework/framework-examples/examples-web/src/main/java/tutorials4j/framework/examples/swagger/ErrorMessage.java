package tutorials4j.framework.examples.swagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 错误消息载体，用于封装一组错误信息并返回给客户端。
 *
 * @author Yun Jiao
 */
@Data
public class ErrorMessage {
  /** 错误信息列表 */
  private List<String> errors;

  /** 无参构造方法。 */
  public ErrorMessage() {}

  /**
   * 使用错误信息列表构造错误消息。
   *
   * @param errors 错误信息列表
   */
  public ErrorMessage(List<String> errors) {
    this.errors = errors;
  }

  /**
   * 使用单条错误信息构造错误消息。
   *
   * @param error 单条错误信息
   */
  public ErrorMessage(String error) {
    this(Collections.singletonList(error));
  }

  /**
   * 使用可变参数形式的错误信息构造错误消息。
   *
   * @param errors 错误信息数组
   */
  public ErrorMessage(String... errors) {
    this(Arrays.asList(errors));
  }
}
