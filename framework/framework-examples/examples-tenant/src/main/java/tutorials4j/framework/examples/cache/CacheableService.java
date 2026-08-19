package tutorials4j.framework.examples.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Spring Cache 缓存示例服务。
 *
 * <p>使用 {@link Cacheable} 注解缓存用户、订单与汽车数据，缓存未命中时模拟从数据库生成随机数据。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class CacheableService {

  /**
   * 获取用户信息，结果缓存在 {@code users} 缓存中。
   *
   * @param userId 用户 ID
   * @return 用户信息字符串
   */
  @Cacheable("users")
  public String getUser(Long userId) {
    String data = "user-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 获取订单信息，结果缓存在 {@code orders} 缓存中。
   *
   * @param orderId 订单 ID
   * @return 订单信息字符串
   */
  @Cacheable("orders")
  public String getOrder(Long orderId) {
    String data = "order-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  /**
   * 获取汽车信息，结果缓存在 {@code cars} 缓存中。
   *
   * @param orderId 汽车 ID
   * @return 汽车信息字符串
   */
  @Cacheable("cars")
  public String getCar(Long orderId) {
    String data = "car-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }
}
