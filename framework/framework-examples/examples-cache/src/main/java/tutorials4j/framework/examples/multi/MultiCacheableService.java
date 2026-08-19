package tutorials4j.framework.examples.multi;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.examples.Car;

/**
 * 多级缓存示例服务。
 *
 * <p>通过 {@link Cacheable} 注解演示多级缓存的存取：首次调用模拟从数据库获取数据，后续调用命中缓存直接返回。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class MultiCacheableService {

  /**
   * 根据用户 ID 查询用户数据，结果缓存于 "users" 缓存。
   *
   * @param userId 用户 ID
   * @return 用户数据
   */
  @Cacheable("users")
  public String getUser(Long userId) {
    String data = "user-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 根据订单 ID 查询订单数据，结果缓存于 "orders" 缓存。
   *
   * @param orderId 订单 ID
   * @return 订单数据
   */
  @Cacheable("orders")
  public String getOrder(Long orderId) {
    String data = "order-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 根据汽车 ID 查询汽车信息，结果缓存于 "cars" 缓存。
   *
   * @param carId 汽车 ID
   * @return 汽车信息
   */
  @Cacheable("cars")
  public Car getCar(Long carId) {
    Car car = new Car(carId, RandomStringUtils.insecure().nextAlphabetic(5), new Date());
    log.info("数据库中获取数据：{}", car);
    return car;
  }
}
