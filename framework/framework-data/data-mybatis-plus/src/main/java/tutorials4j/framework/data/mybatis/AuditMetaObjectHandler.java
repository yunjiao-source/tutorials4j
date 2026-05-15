package tutorials4j.framework.data.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import tutorials4j.framework.data.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class AuditMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "createdBy", String.class, SecurityUtils.getAccount());
    this.strictInsertFill(metaObject, "createdDate", Date.class, new Date());
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "lastModifiedBy", String.class, SecurityUtils.getAccount());
    this.strictUpdateFill(metaObject, "lastModifiedDate", Date.class, new Date());
  }
}
