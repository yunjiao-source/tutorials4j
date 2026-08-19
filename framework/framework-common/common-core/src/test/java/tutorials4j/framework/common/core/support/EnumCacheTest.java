package tutorials4j.framework.common.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Getter;
import org.junit.jupiter.api.Test;

/**
 * {@link EnumCache} 单元测试
 *
 * @author Yun Jiao
 */
public class EnumCacheTest {
  /** 验证 {@link EnumCache} 按名称查找枚举及其默认值兜底功能。 */
  @Test
  void test() {
    assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS")).isEqualTo(StatusEnum.SUCCESS);
    assertThat(EnumCache.findByName(StatusEnum.class, "success")).isNull();

    assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS1")).isNull();

    assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS1", StatusEnum.INIT))
        .isEqualTo(StatusEnum.INIT);
  }

  /** 测试用状态枚举，演示 {@link EnumCache} 按名称与按值构建缓存的功能。 */
  @Getter
  public enum StatusEnum {
    /** 初始化。 */
    INIT("I", "初始化"),
    /** 处理中。 */
    PROCESSING("P", "处理中"),
    /** 成功。 */
    SUCCESS("S", "成功"),
    /** 失败。 */
    FAIL("F", "失败");

    /** 枚举编码。 */
    private String code;

    /** 枚举描述。 */
    private String desc;

    StatusEnum(String code, String desc) {
      this.code = code;
      this.desc = desc;
    }

    static {
      // 通过名称构建缓存,通过EnumCache.findByName(StatusEnum.class,"SUCCESS",null);调用能获取枚举
      EnumCache.registerByName(StatusEnum.class, StatusEnum.values());
      // 通过code构建缓存,通过EnumCache.findByValue(StatusEnum.class,"S",null);调用能获取枚举
      EnumCache.registerByValue(StatusEnum.class, StatusEnum.values(), StatusEnum::getCode);
    }
  }
}
