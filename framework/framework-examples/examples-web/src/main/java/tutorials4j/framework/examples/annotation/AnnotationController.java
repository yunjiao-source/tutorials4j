package tutorials4j.framework.examples.annotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.core.annotation.AccessLimited;
import tutorials4j.framework.web.core.annotation.Idempotent;

/**
 * 框架注解使用示例控制器。
 *
 * <p>演示 {@link Idempotent}（幂等）与 {@link AccessLimited}（访问限制）两个框架注解的用法。
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("annotation")
@RequiredArgsConstructor
public class AnnotationController {

  /**
   * 幂等注解示例接口。
   *
   * @return 固定返回字符串
   */
  @Idempotent
  @GetMapping("idempotent")
  public String idempotent() {
    return "idempotent";
  }

  /**
   * 访问限制注解示例接口，限制最大调用次数为 4。
   *
   * @return 固定返回字符串
   */
  @AccessLimited(maxTimes = 4)
  @GetMapping("access-limited")
  public String accessLimited() {
    return "accessLimited";
  }
}
