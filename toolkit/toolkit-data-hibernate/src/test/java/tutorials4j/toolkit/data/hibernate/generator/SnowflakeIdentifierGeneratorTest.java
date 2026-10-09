package tutorials4j.toolkit.data.hibernate.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Subclass;
import org.hibernate.type.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link SnowflakeIdentifierGenerator} 单元测试：主键类型校验与 ID 生成。
 *
 * @author Yun Jiao
 */
class SnowflakeIdentifierGeneratorTest {

  private static void configure(
      SnowflakeIdentifierGenerator generator, Class<?> idType, String typeName) {
    configure(generator, idType, typeName, new Properties());
  }

  private static void configure(
      SnowflakeIdentifierGenerator generator,
      Class<?> idType,
      String typeName,
      Properties parameters) {
    Type type = mock(Type.class);
    when(type.getReturnedClass()).thenAnswer(invocation -> idType);
    when(type.getName()).thenReturn(typeName);

    Property idProperty = mock(Property.class);
    when(idProperty.getType()).thenReturn(type);

    // PersistentClass 是密封类无法 mock，Subclass 是它的非 final 实现
    Subclass persistentClass = mock(Subclass.class);
    when(persistentClass.getIdentifierProperty()).thenReturn(idProperty);

    GeneratorCreationContext creationContext = mock(GeneratorCreationContext.class);
    when(creationContext.getPersistentClass()).thenReturn(persistentClass);

    generator.configure(creationContext, parameters);
  }

  private static SharedSessionContractImplementor session() {
    return mock(SharedSessionContractImplementor.class);
  }

  @Test
  @DisplayName("主键为 Long 时生成 Long 类型雪花 ID")
  void shouldGenerateLongIdForLongPrimaryKey() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();
    configure(generator, Long.class, "java.lang.Long");

    Object id = generator.generate(session(), new Object());

    assertThat(id).isInstanceOf(Long.class);
    assertThat((Long) id).isPositive();
  }

  @Test
  @DisplayName("主键为 String 时生成字符串类型雪花 ID")
  void shouldGenerateStringIdForStringPrimaryKey() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();
    configure(generator, String.class, "java.lang.String");

    Object id = generator.generate(session(), new Object());

    assertThat(id).isInstanceOf(String.class);
    assertThat((String) id).matches("\\d+");
    assertThat(Long.parseLong((String) id)).isPositive();
  }

  @Test
  @DisplayName("多次生成互不重复且递增")
  void shouldGenerateDistinctIncreasingIds() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();
    configure(generator, Long.class, "java.lang.Long");

    Set<Long> ids = ConcurrentHashMap.newKeySet();
    long previous = Long.MIN_VALUE;
    for (int i = 0; i < 100; i++) {
      long id = (Long) generator.generate(session(), null);
      assertThat(id).isGreaterThan(previous);
      previous = id;
      ids.add(id);
    }

    assertThat(ids).hasSize(100);
  }

  @Test
  @DisplayName("主键为 Integer 时抛出 HibernateException 并提示类型名")
  void shouldRejectIntegerPrimaryKey() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();

    assertThatThrownBy(() -> configure(generator, Integer.class, "java.lang.Integer"))
        .isInstanceOf(HibernateException.class)
        .hasMessageContaining("雪花算法 ID 生成器仅支持 Long 或 String 类型的主键字段")
        .hasMessageContaining("java.lang.Integer");
  }

  @Test
  @DisplayName("主键为 UUID 等不支持的类型时抛出 HibernateException")
  void shouldRejectUuidPrimaryKey() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();

    assertThatThrownBy(() -> configure(generator, UUID.class, "java.util.UUID"))
        .isInstanceOf(HibernateException.class)
        .hasMessageContaining("java.util.UUID");
  }

  @Test
  @DisplayName("配置参数为 null 时不影响初始化")
  void shouldIgnoreNullParameters() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();

    configure(generator, Long.class, "java.lang.Long", null);

    assertThat(generator.generate(session(), null)).isInstanceOf(Long.class);
  }

  @Test
  @DisplayName("未调用 configure 时按非 String 处理，返回 Long 类型 ID")
  void shouldGenerateLongIdWhenNotConfigured() {
    SnowflakeIdentifierGenerator generator = new SnowflakeIdentifierGenerator();

    assertThat(generator.generate(session(), null)).isInstanceOf(Long.class);
  }
}
