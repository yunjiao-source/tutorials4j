package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.Instant;
import org.apache.ibatis.reflection.MetaObject;
import tutorials4j.framework.common.spring.util.SecurityUtils;

/**
 * 审计字段自动填充处理器。
 *
 * <p>实现 MyBatis Plus 的 {@link MetaObjectHandler}，在数据插入与更新时自动填充创建人、 创建时间、最后修改人、最后修改时间等审计字段。
 *
 * @author Yun Jiao
 * @see MetaObjectHandler
 */
public class AuditMetaObjectHandler implements MetaObjectHandler {

  /** 插入数据时自动填充创建人、创建时间以及最后修改时间。 */
  @Override
  public void insertFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "createdBy", String.class, SecurityUtils.getAccount());
    this.strictInsertFill(metaObject, "createdDate", Instant.class, Instant.now());
    this.strictUpdateFill(metaObject, "lastModifiedDate", Instant.class, Instant.now());
  }

  /** 更新数据时自动填充最后修改人与最后修改时间。 */
  @Override
  public void updateFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "lastModifiedBy", String.class, SecurityUtils.getAccount());
    this.strictUpdateFill(metaObject, "lastModifiedDate", Instant.class, Instant.now());
  }
}
