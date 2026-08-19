package tutorials4j.framework.common.core.entity;

/**
 * 支持乐观锁版本控制的实体接口。
 *
 * <p>通过维护版本号实现乐观锁，防止并发更新时出现数据覆盖，版本号通常由持久层在更新时递增。
 *
 * @author Yun Jiao
 */
public interface VersionEntity extends Entity {
  /**
   * 获取版本号。
   *
   * @return 版本号
   */
  Integer getVersion();

  /**
   * 设置版本号。
   *
   * @param version 版本号
   */
  void setVersion(Integer version);
}
