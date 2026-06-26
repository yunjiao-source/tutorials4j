package tutorials4j.framework.data.hibernate.domain;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import tutorials4j.framework.common.core.entity.Entity;

/**
 * 基础 Repository 接口，组合 JPA 常用功能。
 *
 * <p>继承了 {@link JpaRepository} 和 {@link JpaSpecificationExecutor}，提供 CRUD 和动态查询能力。
 *
 * @param <E> 实体类型，必须继承自 {@link Entity}
 * @param <ID> 主键类型，必须实现 {@link Serializable}
 * @author Yun Jiao
 */
@NoRepositoryBean
public interface BaseRepository<E extends Entity, ID extends Serializable>
    extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
  Class<E> getEntityClass();

  Class<ID> getIdClass();
}
