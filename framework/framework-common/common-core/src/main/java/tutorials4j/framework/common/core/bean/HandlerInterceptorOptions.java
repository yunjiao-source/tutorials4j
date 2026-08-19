package tutorials4j.framework.common.core.bean;

import lombok.Data;

/**
 * 拦截器注册选项，用于配置拦截器的包含与排除路径模式。
 *
 * <p>通过 includePathPatterns 指定需要拦截的 URL 路径模式，通过 excludePathPatterns 指定需要排除的 URL 路径模式。
 *
 * @author Yun Jiao
 */
@Data
public class HandlerInterceptorOptions {
  /** 需要拦截的 URL 路径模式。 */
  private String[] includePathPatterns = new String[] {};

  /** 需要排除的 URL 路径模式。 */
  private String[] excludePathPatterns = new String[] {};
}
