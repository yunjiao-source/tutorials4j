package tutorials4j.framework.data.hibernate.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * Hibernate 自定义主键生成器注解。
 *
 * <p>标注在实体类的标识符字段（{@code @Id}）上，将使用 {@link UidentifierGenerator} 生成基于雪花算法的全局唯一 ID。支持 {@link
 * Long}（或基本类型 long）和 {@link String} 类型的主键。
 *
 * <pre>{@code
 * @Entity
 * public class User {
 *     @Id
 *     @UidGenerator
 *     private Long id;
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see UidentifierGenerator
 * @see IdGeneratorType
 */
@IdGeneratorType(UidentifierGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UidGenerator {}
