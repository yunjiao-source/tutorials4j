package tutorials4j.toolkit.ip2region.autoconfigure;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.ip2region.CachePolicy;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_IP2REGION)
public class Ip2RegionToolkitProperties {
  private boolean warmUp = false;
  private Duration closeTimeout = Duration.ofMillis(10000L);
  private String v4xdb = "classpath:/ip2region/ip2region_v4.xdb";
  private String v6xdb = "classpath:/ip2region/ip2region_v6.xdb";
  private Ip2RegionOptions v4 = new Ip2RegionOptions();
  private Ip2RegionOptions v6 = new Ip2RegionOptions();

  @Data
  public static class Ip2RegionOptions {
    private CachePolicy cachePolicy = CachePolicy.VIndexCache;
    private int searchers = 20;
    private DataSize cacheSliceBytes = DataSize.ofMegabytes(50);
    private boolean fairLock = false;
  }
}
