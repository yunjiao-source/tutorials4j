package tutorials4j.toolkit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.hutool.core.lang.Snowflake;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link SnowflakeUtils} 单元测试。
 *
 * <p>工具类内部持有静态 {@link Snowflake} 实例，每个用例前后通过反射清空该字段并清理系统属性， 使 worker/datacenter 的初始化逻辑可重复验证。
 *
 * @author Yun Jiao
 */
class SnowflakeUtilsTest {

  @BeforeEach
  void setUp() {
    resetSnowflake();
    clearProperties();
  }

  @AfterEach
  void tearDown() {
    resetSnowflake();
    clearProperties();
  }

  private void resetSnowflake() {
    setStaticField("snowflake", null);
  }

  private void clearProperties() {
    System.clearProperty(SnowflakeUtils.PRO_WORKER_ID);
    System.clearProperty(SnowflakeUtils.PRO_DATACENTER_ID);
  }

  private void setStaticField(String name, Object value) {
    try {
      Field field = SnowflakeUtils.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(null, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("重置 SnowflakeUtils 状态失败: " + name, e);
    }
  }

  private Snowflake currentSnowflake() {
    try {
      Field field = SnowflakeUtils.class.getDeclaredField("snowflake");
      field.setAccessible(true);
      return (Snowflake) field.get(null);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("读取 SnowflakeUtils 状态失败", e);
    }
  }

  @Test
  @DisplayName("常量定义：worker 与 datacenter 的系统属性名")
  void shouldExposePropertyNames() {
    assertThat(SnowflakeUtils.PRO_WORKER_ID).isEqualTo("TUTORIALS4J_WORKER_ID");
    assertThat(SnowflakeUtils.PRO_DATACENTER_ID).isEqualTo("TUTORIALS4J_DATACENTER_ID");
  }

  @Test
  @DisplayName("工具类不可实例化")
  void shouldNotBeInstantiable() {
    assertThatThrownBy(() -> SnowflakeUtils.class.getDeclaredConstructor().newInstance())
        .isInstanceOf(IllegalAccessException.class);
  }

  @Test
  @DisplayName("nextId 生成唯一且递增的 ID，并初始化雪算法实例")
  void shouldGenerateUniqueIncreasingIds() {
    Set<Long> ids = ConcurrentHashMap.newKeySet();
    long previous = Long.MIN_VALUE;
    int count = 1000;

    for (int i = 0; i < count; i++) {
      long id = SnowflakeUtils.nextId();
      assertThat(id).isGreaterThan(previous);
      previous = id;
      ids.add(id);
    }

    assertThat(ids).hasSize(count);
    assertThat(currentSnowflake()).isNotNull();
  }

  @Test
  @DisplayName("nextIdStr 生成数字字符串且互不相同")
  void shouldGenerateIdString() {
    String first = SnowflakeUtils.nextIdStr();
    String second = SnowflakeUtils.nextIdStr();

    assertThat(first).matches("\\d+");
    assertThat(Long.parseLong(first)).isPositive();
    assertThat(second).isNotEqualTo(first);
  }

  @Test
  @DisplayName("workerId 与 datacenterId 取自系统属性")
  void shouldUseWorkerAndDatacenterFromSystemProperties() {
    System.setProperty(SnowflakeUtils.PRO_WORKER_ID, "5");
    System.setProperty(SnowflakeUtils.PRO_DATACENTER_ID, "7");

    long id = SnowflakeUtils.nextId();
    Snowflake snowflake = currentSnowflake();

    assertThat(snowflake.getWorkerId(id)).isEqualTo(5);
    assertThat(snowflake.getDataCenterId(id)).isEqualTo(7);
  }

  @Test
  @DisplayName("系统属性非数字时回退默认值 1")
  void shouldFallBackToDefaultsWhenPropertyIsNotNumeric() {
    System.setProperty(SnowflakeUtils.PRO_WORKER_ID, "abc");
    System.setProperty(SnowflakeUtils.PRO_DATACENTER_ID, "xyz");

    long id = SnowflakeUtils.nextId();

    assertThat(currentSnowflake().getWorkerId(id)).isEqualTo(1);
    assertThat(currentSnowflake().getDataCenterId(id)).isEqualTo(1);
  }

  @Test
  @DisplayName("系统属性为空或空白时回退默认值 1")
  void shouldFallBackToDefaultsWhenPropertyIsBlank() {
    System.setProperty(SnowflakeUtils.PRO_WORKER_ID, " ");

    long id = SnowflakeUtils.nextId();

    assertThat(currentSnowflake().getWorkerId(id)).isEqualTo(1);
    assertThat(currentSnowflake().getDataCenterId(id)).isEqualTo(1);
  }

  @Test
  @DisplayName("并发调用 nextId 不会产生重复 ID")
  void shouldGenerateUniqueIdsConcurrently() throws InterruptedException {
    int threads = 8;
    int idsPerThread = 200;
    Set<Long> ids = ConcurrentHashMap.newKeySet();
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(threads);

    for (int i = 0; i < threads; i++) {
      new Thread(
              () -> {
                try {
                  start.await();
                  for (int j = 0; j < idsPerThread; j++) {
                    ids.add(SnowflakeUtils.nextId());
                  }
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  finished.countDown();
                }
              })
          .start();
    }

    start.countDown();
    assertThat(finished.await(10, TimeUnit.SECONDS)).isTrue();

    assertThat(ids).hasSize(threads * idsPerThread);
  }
}
