package tutorials4j.toolkit.data.mybatisplus;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import tutorials4j.toolkit.core.util.SnowflakeUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

  @Override
  public String nextUUID(Object entity) {
    return SnowflakeUtils.nextIdStr();
  }

  @Override
  public Number nextId(Object entity) {
    return SnowflakeUtils.nextId();
  }
}
