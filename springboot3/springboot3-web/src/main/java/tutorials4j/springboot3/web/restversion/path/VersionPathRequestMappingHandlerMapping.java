package tutorials4j.springboot3.web.restversion.path;

import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tutorials4j.springboot3.web.restversion.ApiVersion;

/**
 * 自定义处理器映射类
 *
 * @author yangyunjiao
 */
public class VersionPathRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

  @Override
  protected RequestCondition<?> getCustomMethodCondition(Method method) {
    ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
    return apiVersion != null ? new VersionPathRequestCondition(apiVersion.value()) : null;
  }

  @Override
  protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
    ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
    return apiVersion != null ? new VersionPathRequestCondition(apiVersion.value()) : null;
  }
}
