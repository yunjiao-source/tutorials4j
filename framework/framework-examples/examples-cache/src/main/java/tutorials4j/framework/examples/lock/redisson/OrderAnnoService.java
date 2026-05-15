package tutorials4j.framework.examples.lock.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.lock.LockType;
import tutorials4j.framework.cache.redisson.RedissonLockable;

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
public class OrderAnnoService {
    @RedissonLockable(key="#root.args[0]", prefix = "order:")
    public void blockAutoRenewal(String orderId) {
        log.info("blockAutoRenewal - {}", Thread.currentThread().getName());
        int time = sleep();
        log.info("blockAutoRenewal - {}, 时长：{}", Thread.currentThread().getName(), time);
    }

    @RedissonLockable(key="#root.args[0]", prefix = "order:", expireTime = 3)
    public void blockFixedLease(String orderId) {
        log.info("blockFixedLease - {}", Thread.currentThread().getName());
        int time = sleep();
        log.info("blockFixedLease - {}, 时长：{}", Thread.currentThread().getName(), time);
    }

    @RedissonLockable(key="#root.args[0]", prefix = "order:", type = LockType.REENTRANT)
    public void reentrantAutoRenewal(String orderId) {
        log.info("reentrantAutoRenewal - {}", Thread.currentThread().getName());
        int time = sleep();
        log.info("reentrantAutoRenewal - {}, 时长：{}", Thread.currentThread().getName(), time);
    }

    @RedissonLockable(key="#root.args[0]", prefix = "order:", expireTime = 4, type = LockType.REENTRANT)
    public void reentrantFixedLease(String orderId) {
        log.info("reentrantFixedLease - {}", Thread.currentThread().getName());
        int time = sleep();
        log.info("reentrantFixedLease - {}, 时长：{}", Thread.currentThread().getName(), time);
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
