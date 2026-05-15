package tutorials4j.framework.data.hibernate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * 标识一个 Hibernate 实体主键使用雪花算法（Snowflake）生成器。
 *
 * <p>将该注解标注在实体的主键字段或 getter 方法上，Hibernate 在持久化时会调用 {@link SnowflakeIdentifierGenerator} 生成分布式唯一 ID。
 *
 * <p>支持的 ID 类型：
 *
 * <ul>
 *   <li>{@code String}：生成 19 位十进制字符串
 *   <li>{@code Long} 或数值类型：生成 {@code long} 型数值
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * @Entity
 * public class User {
 *     @Id
 *     @SnowflakeIDGenerator
 *     private String id;
 *
 *     // other fields...
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see SnowflakeIdentifierGenerator
 * @since 1.0
 */
@IdGeneratorType(SnowflakeIdentifierGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SnowflakeIDGenerator {}
