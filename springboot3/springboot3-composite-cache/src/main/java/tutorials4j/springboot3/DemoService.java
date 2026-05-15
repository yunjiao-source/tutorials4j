package tutorials4j.springboot3;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class DemoService {

  @Cacheable("users")
  public String getUser(Long userId) {
    String data = "user-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  @Cacheable("orders")
  public String getOrder(Long orderId) {
    String data = "order-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }

  @Cacheable("cars")
  public String getCars(Long carId) {
    String data = "car-" + RandomStringUtils.insecure().nextAlphabetic(5);
    log.info("数据库中获取数据：{}", data);
    return data;
  }
}
