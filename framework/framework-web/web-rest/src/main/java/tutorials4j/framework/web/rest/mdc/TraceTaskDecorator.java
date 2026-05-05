package tutorials4j.framework.web.rest.mdc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class TraceTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 复制当前线程的MDC上下文
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                // 异步任务执行前设置MDC
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Tutorials4j - Web |- 跟踪信息任务装饰器：{}", contextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
