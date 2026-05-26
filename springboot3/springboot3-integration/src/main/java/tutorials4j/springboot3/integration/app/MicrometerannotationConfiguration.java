package tutorials4j.springboot3.integration.app;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("micrometerannotation")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.integration.micrometerannotation"})
public class MicrometerannotationConfiguration {

  // 如果你需要控制只针对某些有特定标识的方法或类进行统计，那么你需要自定义TimedAspect和CountedAspect切面
  @Bean
  public CountedAspect countedAspect(MeterRegistry meterRegistry) {
    return new CountedAspect(meterRegistry, this::skipNonControllers);
  }

  private boolean skipNonControllers(ProceedingJoinPoint pjp) {
    Class<?> targetClass = pjp.getTarget().getClass();
    // @Counted只在接口有效
    return AnnotationUtils.findAnnotation(targetClass, RestController.class) == null;
  }
}
