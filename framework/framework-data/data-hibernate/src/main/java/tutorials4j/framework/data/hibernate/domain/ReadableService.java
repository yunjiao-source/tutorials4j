package tutorials4j.framework.data.hibernate.domain;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.entity.Entity;
import tutorials4j.framework.data.core.exception.DataErrorCode;

/**
 * 只读服务接口，提供基本的查询、分页、计数等功能。
 *
 * @param <E> 实体类型
 * @param <ID> 主键类型
 * @author Yun Jiao
 */
public interface ReadableService<E extends Entity, ID extends Serializable> {

  /**
   * 获取底层 Repository 实例。
   *
   * @return Repository 对象
   */
  BaseRepository<E, ID> getRepository();

  /**
   * 根据主键查找实体，如果不存在则抛出异常。
   *
   * @param id 主键
   * @return 实体
   */
  default E findById(ID id) {
    return getRepository()
        .findById(id)
        .orElseThrow(
            () ->
                DataErrorCode.DATA_SOURCE_NOT_EXIST
                    .throwed()
                    .param("repository", getRepository().getClass().getName())
                    .param("id", id));
  }

  /**
   * 根据主键获取实体引用，不立即加载全部数据。
   *
   * @param id 主键
   * @return 实体引用
   */
  default E getReferenceById(ID id) {
    return getRepository().getReferenceById(id);
  }

  /**
   * 检查主键对应的实体是否存在。
   *
   * @param id 主键
   * @return true 存在，false 不存在
   */
  default boolean existsById(ID id) {
    return getRepository().existsById(id);
  }

  /**
   * 统计全部实体数量。
   *
   * @return 总记录数
   */
  default long count() {
    return getRepository().count();
  }

  /**
   * 根据条件统计实体数量。
   *
   * @param specification 查询条件
   * @return 符合条件的记录数
   */
  default long count(Specification<E> specification) {
    return getRepository().count(specification);
  }

  /**
   * 查询全部实体。
   *
   * @return 实体列表
   */
  default List<E> findAll() {
    return getRepository().findAll();
  }

  /**
   * 查询全部实体并排序。
   *
   * @param sort 排序规则
   * @return 排序后的实体列表
   */
  default List<E> findAll(Sort sort) {
    return getRepository().findAll(sort);
  }

  /**
   * 根据条件查询实体列表。
   *
   * @param specification 查询条件
   * @return 实体列表
   */
  default List<E> findAll(Specification<E> specification) {
    return getRepository().findAll(specification);
  }

  /**
   * 根据条件和排序查询实体列表。
   *
   * @param specification 查询条件
   * @param sort 排序规则
   * @return 实体列表
   */
  default List<E> findAll(Specification<E> specification, Sort sort) {
    return getRepository().findAll(specification, sort);
  }

  /**
   * 使用 example 查询实体列表。
   *
   * @param example 查询示例
   * @return 实体列表
   */
  default List<E> findAll(Example<E> example) {
    return getRepository().findAll(example);
  }

  /**
   * 使用 example 和排序查询实体列表。
   *
   * @param example 查询示例
   * @param sort 排序规则
   * @return 实体列表
   */
  default List<E> findAll(Example<E> example, Sort sort) {
    return getRepository().findAll(example, sort);
  }

  /**
   * 分页查询所有实体。
   *
   * @param pageable 分页参数
   * @return 分页结果
   */
  default Page<E> findByPage(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

  /**
   * 按页码和每页大小分页查询。
   *
   * @param pageNumber 页码（从0开始）
   * @param pageSize 每页记录数
   * @return 分页结果
   */
  default Page<E> findByPage(int pageNumber, int pageSize) {
    return findByPage(PageRequest.of(pageNumber, pageSize));
  }

  /**
   * 分页查询并排序。
   *
   * @param pageNumber 页码
   * @param pageSize 每页大小
   * @param sort 排序规则
   * @return 分页结果
   */
  default Page<E> findByPage(int pageNumber, int pageSize, Sort sort) {
    return findByPage(PageRequest.of(pageNumber, pageSize, sort));
  }

  /**
   * 分页查询并指定排序方向和属性。
   *
   * @param pageNumber 页码
   * @param pageSize 每页大小
   * @param direction 排序方向
   * @param properties 排序属性
   * @return 分页结果
   */
  default Page<E> findByPage(
      int pageNumber, int pageSize, Sort.Direction direction, String... properties) {
    return findByPage(PageRequest.of(pageNumber, pageSize, direction, properties));
  }

  /**
   * 条件分页查询。
   *
   * @param specification 查询条件
   * @param pageable 分页参数
   * @return 分页结果
   */
  default Page<E> findByPage(Specification<E> specification, Pageable pageable) {
    return getRepository().findAll(specification, pageable);
  }

  /**
   * 条件分页查询（页码+每页大小）。
   *
   * @param specification 查询条件
   * @param pageNumber 页码
   * @param pageSize 每页大小
   * @return 分页结果
   */
  default Page<E> findByPage(Specification<E> specification, int pageNumber, int pageSize) {
    return getRepository().findAll(specification, PageRequest.of(pageNumber, pageSize));
  }

  /**
   * 分页查询并指定排序方向（不指定排序属性时使用默认）。
   *
   * @param pageNumber 页码
   * @param pageSize 每页大小
   * @param direction 排序方向
   * @return 分页结果
   */
  default Page<E> findByPage(int pageNumber, int pageSize, Sort.Direction direction) {
    return findByPage(PageRequest.of(pageNumber, pageSize, direction));
  }
}
