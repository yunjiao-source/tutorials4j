package tutorials4j.springboot3.web.restversion.header;

import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tutorials4j.springboot3.web.restversion.ApiVersion;

/**
 * 自定义处理器映射类
 *
 * @author yangyunjiao
 */
public class VersionHeaderRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

  /** 为每个接口创建版本匹配条件 */
  @Override
  protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
    // 提取类上的@ApiVersion注解
    ApiVersion apiVersion =
        AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
    return apiVersion != null ? new VersionHeaderRequestCondition(apiVersion.value()) : null;
  }

  /** 为每个方法创建版本匹配条件（优先级高于类） */
  @Override
  protected RequestCondition<?> getCustomMethodCondition(Method method) {
    // 提取方法上的@ApiVersion注解
    ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
    return apiVersion != null ? new VersionHeaderRequestCondition(apiVersion.value()) : null;
  }
}
