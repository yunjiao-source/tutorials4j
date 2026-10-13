package tutorials4j.toolkit.json.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * {@link ToolkitSimpleModule} 单元测试：Long 序列化为字符串的定制行为。
 *
 * @author YunJiao Yang
 */
class ToolkitSimpleModuleTest {
  private final ToolkitSimpleModule module = new ToolkitSimpleModule();
  private final ObjectMapper mapper =
      JsonMapper.builder().addModule(new ToolkitSimpleModule()).build();

  static class ValueHolder {
    private long primitiveValue;
    private Long boxedValue;
    private Integer intValue;

    public long getPrimitiveValue() {
      return primitiveValue;
    }

    public void setPrimitiveValue(long primitiveValue) {
      this.primitiveValue = primitiveValue;
    }

    public Long getBoxedValue() {
      return boxedValue;
    }

    public void setBoxedValue(Long boxedValue) {
      this.boxedValue = boxedValue;
    }

    public Integer getIntValue() {
      return intValue;
    }

    public void setIntValue(Integer intValue) {
      this.intValue = intValue;
    }
  }

  @Test
  @DisplayName("Long 序列化为字符串")
  void shouldSerializeLongAsString() {
    assertThat(mapper.writeValueAsString(1L)).isEqualTo("\"1\"");
    assertThat(mapper.writeValueAsString(Long.valueOf(42))).isEqualTo("\"42\"");
  }

  @Test
  @DisplayName("基本类型 long 与包装类型 Long 字段都序列化为字符串")
  void shouldSerializeLongFieldsAsString() {
    ValueHolder holder = new ValueHolder();
    holder.setPrimitiveValue(1L);
    holder.setBoxedValue(2L);

    String json = mapper.writeValueAsString(holder);
    Map<String, Object> parsed =
        mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

    // 属性顺序由 Jackson 决定，此处只断言取值形式
    assertThat(parsed)
        .containsEntry("primitiveValue", "1")
        .containsEntry("boxedValue", "2")
        .containsEntry("intValue", null);
  }

  @Test
  @DisplayName("Integer/int 仍按数字序列化")
  void shouldKeepIntegerAsNumber() {
    ValueHolder holder = new ValueHolder();
    holder.setIntValue(7);

    assertThat(mapper.writeValueAsString(7)).isEqualTo("7");
    assertThat(mapper.writeValueAsString(holder)).contains("\"intValue\":7");
  }

  @Test
  @DisplayName("Long.MAX_VALUE 不丢失精度（前端 JS 数值溢出的规避目标）")
  void shouldPreserveLongMaxValue() {
    String json = mapper.writeValueAsString(Long.MAX_VALUE);

    assertThat(json).isEqualTo("\"9223372036854775807\"");
    assertThat(mapper.readValue(json, Long.class)).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  @DisplayName("集合与 Map 中的 Long 元素序列化为字符串")
  void shouldSerializeLongInCollections() {
    assertThat(mapper.writeValueAsString(List.of(1L, 2L))).isEqualTo("[\"1\",\"2\"]");
    assertThat(mapper.writeValueAsString(Map.of("id", 1L))).isEqualTo("{\"id\":\"1\"}");
  }

  @Test
  @DisplayName("null 的 Long 值仍序列化为 null")
  void shouldSerializeNullLongAsNull() {
    assertThat(mapper.writeValueAsString((Long) null)).isEqualTo("null");
    assertThat(mapper.writeValueAsString(new ValueHolder())).contains("\"boxedValue\":null");
  }

  @Test
  @DisplayName("字符串形式可反序列化回 Long")
  void shouldDeserializeStringBackToLong() {
    ValueHolder holder = mapper.readValue("{\"boxedValue\":\"5\"}", ValueHolder.class);

    assertThat(holder.getBoxedValue()).isEqualTo(5L);
  }

  @Test
  @DisplayName("模块已注册到 ObjectMapper，执行顺序为 100")
  void shouldBeRegisteredWithOrder() {
    assertThat(mapper.registeredModules())
        .anyMatch(registered -> registered instanceof ToolkitSimpleModule);

    assertThat(module).isInstanceOf(Ordered.class);
    assertThat(module.getOrder()).isEqualTo(100);
  }
}
