package tutorials4j.toolkit.data.mybatisplus.component;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import tutorials4j.toolkit.data.mybatisplus.support.SnowflakeIdentifierGenerator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class IdentifierMybatisPlusPropertiesCustomizer implements MybatisPlusPropertiesCustomizer {

  @Override
  public void customize(MybatisPlusProperties properties) {
    properties.getGlobalConfig().setIdentifierGenerator(new SnowflakeIdentifierGenerator());
  }
}
