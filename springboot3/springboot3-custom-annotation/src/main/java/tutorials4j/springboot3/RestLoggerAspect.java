package tutorials4j.springboot3;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 日志切面类：处理@Log注解的逻辑
 *
 * @author Yun Jiao
 */
@Slf4j
@Aspect // 标识为切面类
@Component // 交给Spring管理
public class RestLoggerAspect {

    // 切入点：匹配所有被@Log注解标记的方法
    @Pointcut("@annotation(restLogger)")
    public void logPointCut(RestLogger restLogger) {}

    // 环绕通知：在方法执行前后执行日志记录逻辑
    @Around(value = "logPointCut(restLogger)", argNames = "joinPoint,restLogger")
    public Object invoke(ProceedingJoinPoint joinPoint, RestLogger restLogger) throws Throwable {
        // 1. 记录开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 2. 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // 3. 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 4. 打印日志（请求信息）
            log.info("===== 接口开始 =====");
            log.info("接口描述：{}", restLogger.value());
            log.info("请求URL：{}", request.getRequestURL().toString());
            log.info("请求方式：{}", request.getMethod());
            log.info("请求方法：{}.{}", joinPoint.getTarget().getClass().getName(), method.getName());

            // 5. 打印请求参数（根据注解配置）
            if (restLogger.printParam()) {
                log.info("请求参数：{}", Arrays.toString(joinPoint.getArgs()));
            }

            // 6. 执行目标方法（核心业务逻辑）
            Object result = joinPoint.proceed();

            // 7. 打印响应结果（根据注解配置）
            if (restLogger.printResult()) {
                log.info("响应结果：{}", result);
            }

            // 8. 打印执行时间
            log.info("接口耗时：{}ms", System.currentTimeMillis() - startTime);
            log.info("===== 接口结束 =====");

            return result;
        } catch (Exception e) {
            // 9. 异常处理
            log.error("接口执行异常，耗时：{}ms，异常信息：{}", System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw e; // 抛出异常，不影响业务层异常处理
        }
    }
}
