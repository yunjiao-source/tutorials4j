package tutorials4j.springboot3;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
  // 自定义业务状态码（可选，默认使用ResultCode.BUSINESS_ERROR）
  private Integer code;

  // 构造方法1：仅传入提示信息
  public BusinessException(String message) {
    super(message);
  }

  // 构造方法2：传入状态码和提示信息
  public BusinessException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  // 构造方法3：传入状态码枚举和提示信息
  public BusinessException(ResultCode resultCode, String message) {
    super(message);
    this.code = resultCode.getCode();
  }

  // 构造方法4：仅传入状态码枚举
  public BusinessException(ResultCode resultCode) {
    super(resultCode.getMessage());
    this.code = resultCode.getCode();
  }
}
