package tutorials4j.framework.core.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 多线程工具
 *
 * @author Yun Jiao
 */
public class ConcurrentUtils {
    /**
     * 执行一组 Runnable 任务，限制最大并发数
     *
     * @param tasks     任务列表
     * @param threadNum 并发线程数
     * @throws InterruptedException 如果等待过程中被中断
     */
    public static void executeTask(List<Runnable> tasks, int threadNum) throws InterruptedException {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        // 1. 创建固定大小的线程池，控制并发数
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);

        // 2. 创建计数器，初始值为任务总数，用于等待所有任务完成
        CountDownLatch latch = new CountDownLatch(tasks.size());

        try {
            for (Runnable task : tasks) {
                // 3. 包装任务，确保任务执行完后计数器减一
                executor.submit(() -> {
                    try {
                        task.run();
                    } finally {
                        // 无论任务成功还是异常，都要释放锁
                        latch.countDown();
                    }
                });
            }

            // 4. 主线程阻塞，直到所有任务完成
            latch.await();

        } finally {
            // 5. 关闭线程池，防止资源泄露
            shutdown(executor);
        }
    }

    /**
     * 优雅关闭线程池
     */
    public static void shutdown(ExecutorService executor) {
        if (executor != null) {
            executor.shutdown();
            try {
                // 等待已提交的任务终止
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 如果超时，强制关闭
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // 如果当前线程被中断，立即强制关闭
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
