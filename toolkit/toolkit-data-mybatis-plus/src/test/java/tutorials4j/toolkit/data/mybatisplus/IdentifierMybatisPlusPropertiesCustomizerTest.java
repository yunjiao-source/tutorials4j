package tutorials4j.toolkit.data.mybatisplus;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link IdentifierMybatisPlusPropertiesCustomizer} 单元测试：向全局配置注入雪花 ID 生成器。
 *
 * @author YunJiao Yang
 */
class IdentifierMybatisPlusPropertiesCustomizerTest {

  private final IdentifierMybatisPlusPropertiesCustomizer customizer =
      new IdentifierMybatisPlusPropertiesCustomizer();

  @Test
  @DisplayName("实现 MyBatis-Plus 的 MybatisPlusPropertiesCustomizer")
  void shouldImplementCustomizer() {
    assertThat(customizer).isInstanceOf(MybatisPlusPropertiesCustomizer.class);
  }

  @Test
  @DisplayName("customize 将全局 ID 生成器替换为雪花算法实现")
  void shouldReplaceIdentifierGenerator() {
    MybatisPlusProperties properties = new MybatisPlusProperties();

    customizer.customize(properties);

    assertThat(properties.getGlobalConfig()).isNotNull();
    assertThat(properties.getGlobalConfig().getIdentifierGenerator())
        .isInstanceOf(SnowflakeIdentifierGenerator.class);
  }

  @Test
  @DisplayName("customize 覆盖已有的 ID 生成器")
  void shouldOverrideExistingIdentifierGenerator() {
    MybatisPlusProperties properties = new MybatisPlusProperties();
    properties.getGlobalConfig().setIdentifierGenerator(new DefaultIdentifierGenerator());

    customizer.customize(properties);

    assertThat(properties.getGlobalConfig().getIdentifierGenerator())
        .isInstanceOf(SnowflakeIdentifierGenerator.class);
  }

  @Test
  @DisplayName("注入后的生成器可正常生成 ID")
  void shouldProvideWorkingGenerator() {
    MybatisPlusProperties properties = new MybatisPlusProperties();

    customizer.customize(properties);

    assertThat(properties.getGlobalConfig().getIdentifierGenerator().nextId(null))
        .isInstanceOf(Long.class);
  }
}
