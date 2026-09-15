package tutorials4j.toolkit.data.mybatisplus;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class IdentifierMybatisPlusPropertiesCustomizer implements MybatisPlusPropertiesCustomizer {

  @Override
  public void customize(MybatisPlusProperties properties) {
    properties.getGlobalConfig().setIdentifierGenerator(new SnowflakeIdentifierGenerator());
  }
}
