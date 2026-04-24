package tutorials4j.framework.common.core.util;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link EnumCache} 单元测试
 *
 * @author Yun Jiao
 */
public class EnumCacheTest {
    @Test
    void test() {
        assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS")).isEqualTo(StatusEnum.SUCCESS);
        assertThat(EnumCache.findByName(StatusEnum.class, "success")).isNull();

        assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS1")).isNull();

        assertThat(EnumCache.findByName(StatusEnum.class, "SUCCESS1", StatusEnum.INIT)).isEqualTo(StatusEnum.INIT);
    }


    @Getter
    public enum StatusEnum {
        INIT("I", "初始化"),
        PROCESSING("P", "处理中"),
        SUCCESS("S", "成功"),
        FAIL("F", "失败");

        private String code;
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
