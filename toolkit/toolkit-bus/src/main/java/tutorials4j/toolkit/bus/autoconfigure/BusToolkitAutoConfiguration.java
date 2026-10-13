package tutorials4j.toolkit.bus.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@RemoteApplicationEventScan(basePackages = "${spring.cloud.bus.base-packages}")
public class BusToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-BUS] Bus Toolkit Auto Configuration");
  }
}
