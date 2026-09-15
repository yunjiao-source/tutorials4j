package tutorials4j.toolkit.data.domain;

/**
 * TODO
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
