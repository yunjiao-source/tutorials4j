package tutorials4j.framework.examples.lock.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.redisson.BlockRedissonLock;
import tutorials4j.framework.cache.redisson.ReentrantRedissonLock;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 订单
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final BlockRedissonLock blockRedissonLock;
    private final ReentrantRedissonLock reentrantRedissonLock;

    public void blockAutoRenewal(String orderId) {
        String lockKey = "order:" + orderId;
        blockRedissonLock.autoRenewal().doInLock(lockKey, () -> {
            log.info("blockAutoRenewal - {}", Thread.currentThread().getName());
            int time = sleep();
            log.info("blockAutoRenewal - {}, 时长：{}", Thread.currentThread().getName(), time);
        });
    }

    public void blockFixedLease(String orderId) {
        String lockKey = "order:" + orderId;
        blockRedissonLock.fixedLease().doInLock(lockKey, Duration.ofSeconds(3), () -> {
            log.info("blockFixedLease - {}", Thread.currentThread().getName());
            int time = sleep();
            log.info("blockFixedLease - {}, 时长：{}", Thread.currentThread().getName(), time);
        });
    }

    public void reentrantAutoRenewal(String orderId) {
        String lockKey = "order:" + orderId;
        reentrantRedissonLock.autoRenewal().doInLock(lockKey, Duration.ofSeconds(3), () -> {
            log.info("reentrantAutoRenewal - {}", Thread.currentThread().getName());
            int time = sleep();
            log.info("reentrantAutoRenewal - {}, 时长：{}", Thread.currentThread().getName(), time);
        });
    }

    public void reentrantFixedLease(String orderId) {
        String lockKey = "order:" + orderId;
        reentrantRedissonLock.fixedLease().doInLock(lockKey, Duration.ofSeconds(3), Duration.ofSeconds(5), () -> {
            log.info("reentrantFixedLease - {}", Thread.currentThread().getName());
            int time = sleep();
            log.info("reentrantFixedLease - {}, 时长：{}", Thread.currentThread().getName(), time);
        });
    }

    private int sleep() {
        try {
            int seconds = ThreadLocalRandom.current().nextInt(10);
            TimeUnit.SECONDS.sleep(seconds);
            return seconds;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
