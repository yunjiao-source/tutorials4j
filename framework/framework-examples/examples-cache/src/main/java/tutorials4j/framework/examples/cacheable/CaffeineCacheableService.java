package tutorials4j.framework.examples.cacheable;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.examples.Car;

/**
 * Caffeine 缓存示例服务，通过 {@link Cacheable} 注解演示方法级缓存。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@CacheConfig(cacheManager = "caffeineCacheManager")
public class CaffeineCacheableService {

  /**
   * 查询用户数据，结果按 "users" 缓存。
   *
   * @param userId 用户 ID
   * @return 随机生成的用户数据
   */
  @Cacheable("users")
  public String getUser(Long userId) {
    String data = "user-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 查询订单数据，结果按 "orders" 缓存。
   *
   * @param orderId 订单 ID
   * @return 随机生成的订单数据
   */
  @Cacheable("orders")
  public String getOrder(Long orderId) {
    String data = "order-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 查询汽车对象，结果按 "cars" 缓存。
   *
   * @param carId 汽车 ID
   * @return 随机生成的汽车对象
   */
  @Cacheable("cars")
  public Car getCar(Long carId) {
    Car car = new Car(carId, RandomStringUtils.insecure().nextAlphabetic(5), new Date());
    log.info("数据库中获取数据：{}", car);
    return car;
  }
}
