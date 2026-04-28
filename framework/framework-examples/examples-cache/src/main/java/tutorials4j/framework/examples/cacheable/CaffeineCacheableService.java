package tutorials4j.framework.examples.cacheable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.examples.Car;

import java.util.Date;

/**
 * 服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@CacheConfig(cacheManager = "caffeineCacheManager")
public class CaffeineCacheableService {

    @Cacheable("users")
    public String getUser(Long userId) {
        String data = "user-" + RandomStringUtils.insecure().nextAlphabetic(5);
        log.info("数据库中获取数据：{}", data);
        return data;
    }

    @Cacheable("orders")
    public String getOrder(Long orderId) {
        String data =  "order-" + RandomStringUtils.insecure().nextAlphabetic(5);
        log.info("数据库中获取数据：{}", data);
        return data;
    }

    @Cacheable("cars")
    public Car getCar(Long carId) {
        Car car = new Car(carId, RandomStringUtils.insecure().nextAlphabetic(5), new Date());
        log.info("数据库中获取数据：{}", car);
        return car;
    }
}

