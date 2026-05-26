package tutorials4j.springboot3.web.restresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定义统一返回状态码
 *
 * @author Yun Jiao
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
  // 成功状态
  SUCCESS(200, "操作成功"),
  // 客户端异常（4xx）
  BAD_REQUEST(400, "请求参数错误"),
  UNAUTHORIZED(401, "未授权（token无效/未登录）"),
  FORBIDDEN(403, "禁止访问（权限不足）"),
  NOT_FOUND(404, "资源不存在"),
  // 服务器异常（5xx）
  INTERNAL_SERVER_ERROR(500, "服务器内部异常"),
  // 业务异常（自定义）
  BUSINESS_ERROR(600, "业务逻辑异常"),
  DATA_NULL(601, "查询数据为空"),
  PARAM_NULL(602, "必填参数为空");
  // 状态码
  private final Integer code;
  // 状态描述
  private final String message;
}
