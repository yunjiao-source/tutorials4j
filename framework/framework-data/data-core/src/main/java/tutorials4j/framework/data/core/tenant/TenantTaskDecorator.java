package tutorials4j.framework.data.core.tenant;

import org.springframework.core.task.TaskDecorator;
import tutorials4j.framework.common.core.bean.TenantContextHolder;

/**
 * 租户多线程装饰器
 *
 * @author Yun Jiao
 */
public class TenantTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String tenant = TenantContextHolder.get();
        return () -> {
            try {
                // 设置租户
                TenantContextHolder.set(tenant);
                runnable.run();
            } finally {
                TenantContextHolder.clear();
            }
        };
    }
}
