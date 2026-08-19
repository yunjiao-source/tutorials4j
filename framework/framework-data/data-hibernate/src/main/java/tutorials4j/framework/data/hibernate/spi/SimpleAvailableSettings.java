package tutorials4j.framework.data.hibernate.spi;

import org.hibernate.cfg.AvailableSettings;

/**
 * 框架自定义的 Hibernate 可用设置项。
 *
 * <p>在 {@link AvailableSettings} 的基础上扩展框架自身使用的配置键常量。
 *
 * @author Yun Jiao
 */
public interface SimpleAvailableSettings extends AvailableSettings {
  /** 二级缓存类型配置键。 */
  String CACHE_TYPE = "tutorials4j.hibernate.cache_type";
}
