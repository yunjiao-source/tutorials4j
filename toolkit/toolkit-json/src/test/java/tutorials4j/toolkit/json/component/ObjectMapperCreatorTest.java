package tutorials4j.toolkit.json.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * {@link ObjectMapperCreator} 单元测试：默认实例复用、拷贝实例独立性与配置继承。
 *
 * @author YunJiao Yang
 */
class ObjectMapperCreatorTest {
  private static final String NAME_KEY = "\"name\"";
  private static final String ALICE = "\"alice\"";

  private static ObjectMapper plainMapper() {
    return new ObjectMapper();
  }

  private static ObjectMapper mapperWithToolkitModule() {
    return JsonMapper.builder().addModule(new ToolkitSimpleModule()).build();
  }

  @Test
  @DisplayName("getInstance 始终返回同一默认实例")
  void shouldReturnSameDefaultInstance() {
    ObjectMapperCreator creator = new ObjectMapperCreator(plainMapper());

    ObjectMapper first = creator.getInstance();

    assertThat(first).isNotNull();
    assertThat(creator.getInstance()).isSameAs(first);
  }

  @Test
  @DisplayName("newInstance 每次返回不同的新实例")
  void shouldCreateNewInstanceEachTime() {
    ObjectMapperCreator creator = new ObjectMapperCreator(plainMapper());

    ObjectMapper first = creator.newInstance();
    ObjectMapper second = creator.newInstance();

    assertThat(first).isNotNull();
    assertThat(second).isNotSameAs(first);
    assertThat(second).isNotSameAs(creator.getInstance());
  }

  @Test
  @DisplayName("getBeanClass 返回 ObjectMapper")
  void shouldExposeBeanClass() {
    assertThat(new ObjectMapperCreator(plainMapper()).getBeanClass()).isEqualTo(ObjectMapper.class);
  }

  @Test
  @DisplayName("拷贝实例继承源 ObjectMapper 的配置与模块")
  void shouldInheritConfigurationFromSourceMapper() {
    ObjectMapperCreator creator = new ObjectMapperCreator(mapperWithToolkitModule());

    assertThat(creator.getInstance().writeValueAsString(1L)).isEqualTo("\"1\"");
    assertThat(creator.newInstance().writeValueAsString(1L)).isEqualTo("\"1\"");
  }

  @Test
  @DisplayName("构造时拷贝源 ObjectMapper，基准实例与源不是同一对象")
  void shouldCopySourceMapperOnConstruction() {
    ObjectMapper source = plainMapper();
    ObjectMapperCreator creator = new ObjectMapperCreator(source);

    assertThat(creator.getInstance()).isNotSameAs(source);
    assertThat(creator.newInstance()).isNotSameAs(source);
  }

  @Test
  @DisplayName("默认实例与新实例序列化结果一致")
  void shouldProduceEquivalentJson() {
    ObjectMapperCreator creator = new ObjectMapperCreator(plainMapper());
    Map<String, Object> value = Map.of("name", "alice");

    assertThat(creator.newInstance().writeValueAsString(value))
        .isEqualTo(creator.getInstance().writeValueAsString(value))
        .contains(NAME_KEY)
        .contains(ALICE);
  }
}
