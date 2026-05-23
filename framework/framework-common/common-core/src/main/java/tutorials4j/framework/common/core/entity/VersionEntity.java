package tutorials4j.framework.common.core.entity;

/**
 * 支持乐观锁版本控制的实体接口。
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
