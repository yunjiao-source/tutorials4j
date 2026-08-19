package tutorials4j.framework.common.core.entity;

import java.io.Serializable;

/**
 * 具有主键标识的实体接口。
 *
 * <p>通过 {@link #getId()} 获取主键，主键为 {@code null} 时视为尚未持久化的新实体。
 *
 * @param <ID> 主键类型，必须实现 {@link Serializable}
 * @author Yun Jiao
 */
public interface IdEntity<ID extends Serializable> extends Entity {
  /**
   * 获取实体主键。
   *
   * @return 主键值
   */
  ID getId();

  /**
   * 设置实体主键。
   *
   * @param pk 主键值
   */
  void setId(ID pk);

  /**
   * 判断实体是否为新建状态（主键为 {@code null}）。
   *
   * @return true 表示尚未持久化（主键为 null），false 表示已持久化
   */
  default boolean isNew() {
    return getId() == null;
  }
}
