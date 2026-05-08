package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import tutorials4j.framework.common.core.util.SnowflakeUtils;

/**
 * MyBatis-Plus 默认的标识符生成器实现。
 * <p>
 * 该生成器基于 {@link SnowflakeUtils} 工具类生成雪花算法 ID，
 * 支持生成 {@link Number} 类型的数值 ID 和字符串形式的 ID（{@link String}）。
 * </p>
 * <p>
 * 配置方式：通过 MyBatis-Plus 全局配置的 {@code identifierGenerator} 属性注入。
 * </p>
 *
 * @author Yun Jiao
 * @see IdentifierGenerator
 * @see SnowflakeUtils
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeUtils.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return SnowflakeUtils.nextIdStr();
    }
}
