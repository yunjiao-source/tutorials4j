package tutorials4j.springboot3;

import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 权限切面类
 *
 * @author Yun Jiao
 */
@Aspect
@Component
public class NeedPermissionAspect {

  // 切入点
  @Pointcut("@annotation(needPermission)")
  public void permissionPointCut(NeedPermission needPermission) {}

  @Around(value = "permissionPointCut(needPermission)", argNames = "joinPoint,needPermission")
  public Object invoke(ProceedingJoinPoint joinPoint, NeedPermission needPermission)
      throws Throwable {
    String[] requiredPermissions = needPermission.value();
    List<String> requiredPermissionList = Arrays.asList(requiredPermissions);

    // 2. 获取当前用户的权限（实际开发中从Token/Redis中获取）
    // 这里模拟：当前用户拥有的权限为 ["user:query", "order:query"]
    List<String> userPermissions = Arrays.asList("user:query", "order:query");

    // 3. 权限校验
    boolean hasPermission = userPermissions.containsAll(requiredPermissionList);

    if (!hasPermission) {
      // 无权限，抛出异常（后续可通过全局异常处理器返回403）
      throw new RuntimeException("无权限访问，需要权限：" + requiredPermissionList);
    }

    // 4. 有权限，执行目标方法
    return joinPoint.proceed();
  }
}
