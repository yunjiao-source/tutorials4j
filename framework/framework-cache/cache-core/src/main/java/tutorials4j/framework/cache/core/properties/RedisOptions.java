package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.cache.CacheProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RedisOptions extends CacheProperties.Redis{
}
