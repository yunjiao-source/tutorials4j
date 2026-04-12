package tutorials4j.framework.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ConcurrentUtils} 单元测试
 *
 * @author Yun Jiao
 */
public class ConcurrentUtilsTest {

    @Test
    public void testExecuteTask() {
        List<Runnable> tasks = new ArrayList<>();

        // 模拟 10 个任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            tasks.add(() -> {
                System.out.println("任务 " + taskId + " 开始执行，线程: " + Thread.currentThread().getName());
                try {
                    // 模拟耗时操作 (随机 1-3 秒)
                    Thread.sleep((long) (Math.random() * 2000 + 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任务 " + taskId + " 执行完成");
            });
        }

        System.out.println("开始执行，设定并发数: 3");
        long startTime = System.currentTimeMillis();

        try {
            // 设定并发数为 3
            ConcurrentUtils.executeTask(tasks, 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("所有任务执行完毕，总耗时: " + (endTime - startTime) + " ms");
    }
}
