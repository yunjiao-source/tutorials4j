package tutorials4j.framework.data.hibernate.domain;

import java.io.Serializable;
import java.util.List;
import tutorials4j.framework.common.core.entity.Entity;

/**
 * 可写服务接口，在 {@link ReadableService} 基础上提供保存、删除等写入操作。
 *
 * <p>所有默认方法均委托给底层 {@link BaseRepository} 的对应方法，实现类仅需提供 {@link #getRepository()} 即可获得完整的写入能力。
 *
 * @param <E> 实体类型
 * @param <ID> 主键类型
 * @author Yun Jiao
 */
public interface WriteableService<E extends Entity, ID extends Serializable>
    extends ReadableService<E, ID> {

  /**
   * 删除一个实体。
   *
   * @param entity 要删除的实体
   */
  default void delete(E entity) {
    getRepository().delete(entity);
  }

  /** 批量删除所有实体（直接使用数据库批量删除）。 */
  default void deleteAllInBatch() {
    getRepository().deleteAllInBatch();
  }

  /**
   * 批量删除给定的实体集合。
   *
   * @param entities 实体集合
   */
  default void deleteAll(Iterable<E> entities) {
    getRepository().deleteAll(entities);
  }

  /** 删除所有实体。 */
  default void deleteAll() {
    getRepository().deleteAll();
  }

  /**
   * 根据主键删除实体。
   *
   * @param id 主键
   */
  default void deleteById(ID id) {
    getRepository().deleteById(id);
  }

  /**
   * 保存一个实体（插入或更新）。
   *
   * @param domain 实体对象
   * @return 保存后的实体
   */
  default E save(E domain) {
    return getRepository().save(domain);
  }

  /**
   * 批量保存给定的实体集合。
   *
   * @param entities 实体集合
   * @param <S> 实体子类型
   * @return 保存后的实体列表
   */
  default <S extends E> List<S> saveAll(Iterable<S> entities) {
    return getRepository().saveAll(entities);
  }

  /**
   * 保存并立即刷新（同步到数据库）。
   *
   * @param entity 实体对象
   * @return 保存后的实体
   */
  default E saveAndFlush(E entity) {
    return getRepository().saveAndFlush(entity);
  }

  /**
   * 批量保存并立即刷新（同步到数据库）。
   *
   * @param entities 实体列表
   * @return 保存后的实体列表
   */
  default List<E> saveAllAndFlush(List<E> entities) {
    return getRepository().saveAllAndFlush(entities);
  }

  /** 强制将持久化上下文中的更改刷新到数据库。 */
  default void flush() {
    getRepository().flush();
  }
}
