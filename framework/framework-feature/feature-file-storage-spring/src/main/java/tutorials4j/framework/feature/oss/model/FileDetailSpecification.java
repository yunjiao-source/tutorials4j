package tutorials4j.framework.feature.oss.model;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * 文件详情查询的JPA Specification工厂。
 *
 * <p>提供各个字段的查询条件构造方法，支持模糊查询和精确匹配。
 *
 * @author Yun Jiao
 */
public class FileDetailSpecification {

  /**
   * 文件名模糊查询（包含like）。
   *
   * @param filename 文件名（支持通配符处理）
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> filenameLike(String filename) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(filename)) {
        return cb.conjunction();
      }
      return cb.like(root.get("filename"), like(filename));
    };
  }

  /**
   * 平台精确匹配。
   *
   * @param platform 平台标识
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> platformEqual(String platform) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(platform)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("platform"), platform);
    };
  }

  /**
   * 对象ID精确匹配。
   *
   * @param objectId 对象ID
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> objectIdEqual(String objectId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(objectId)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("objectId"), objectId);
    };
  }

  /**
   * 对象类型精确匹配。
   *
   * @param objectType 对象类型
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> objectTypeEqual(String objectType) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(objectType)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("objectType"), objectType);
    };
  }

  /**
   * 创建时间大于等于指定时间。
   *
   * @param createTimeBegin 开始时间
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> createTimeBegin(Date createTimeBegin) {
    return (root, query, cb) -> {
      if (createTimeBegin == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createTime"), createTimeBegin);
    };
  }

  /**
   * 创建时间小于等于指定时间。
   *
   * @param createTimeEnd 结束时间
   * @return Specification对象
   */
  public static Specification<FileDetailEntity> createTimeEnd(Date createTimeEnd) {
    return (root, query, cb) -> {
      if (createTimeEnd == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createTime"), createTimeEnd);
    };
  }
}
