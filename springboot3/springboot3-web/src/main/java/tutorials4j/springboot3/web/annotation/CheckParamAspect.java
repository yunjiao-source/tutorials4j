package tutorials4j.springboot3.web.annotation;

import java.lang.reflect.Field;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 参数校验切面类
 *
 * @author Yun Jiao
 */
@Aspect
@Component
public class CheckParamAspect {

  // 切入点
  @Pointcut("execution(* tutorials4j.springboot3.CheckParamController.*(..))")
  public void paramCheckPointCut() {}

  @Around("paramCheckPointCut()")
  public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
    // 1. 获取方法参数
    Object[] args = joinPoint.getArgs();
    if (args == null) {
      return joinPoint.proceed();
    }

    // 2. 遍历参数，校验带有@CheckParam注解的字段
    for (Object arg : args) {
      if (arg == null) {
        continue;
      }
      checkParam(arg);
    }

    // 3. 校验通过，执行目标方法
    return joinPoint.proceed();
  }

  /** 核心校验逻辑 */
  private void checkParam(Object obj) throws IllegalAccessException {
    // 获取对象的所有字段（包括私有字段）
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      // 开启字段访问权限
      field.setAccessible(true);
      CheckParam checkParam = field.getAnnotation(CheckParam.class);
      if (checkParam == null) {
        continue; // 没有注解的字段不校验
      }
      // 获取字段值
      Object fieldValue = field.get(obj);
      String fieldName = field.getName();
      String message = checkParam.message().isEmpty() ? fieldName + "校验失败" : checkParam.message();

      // 1. 非空校验
      if (checkParam.notNull() && fieldValue == null) {
        throw new RuntimeException(message + "：不能为空");
      }

      // 2. 长度校验（仅针对字符串）
      if (fieldValue instanceof String strValue) {
        if (strValue.length() < checkParam.minLength()) {
          throw new RuntimeException(message + "：长度不能小于" + checkParam.minLength());
        }
        if (strValue.length() > checkParam.maxLength()) {
          throw new RuntimeException(message + "：长度不能大于" + checkParam.maxLength());
        }
      }
    }
  }
}
