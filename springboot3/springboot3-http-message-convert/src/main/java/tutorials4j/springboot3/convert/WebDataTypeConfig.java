package tutorials4j.springboot3.convert;

import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册格式化器
 *
 * @author Yun Jiao
 */
@Component
public class WebDataTypeConfig implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatterForFieldAnnotation(new StringToUserFormatter());
  }
}
