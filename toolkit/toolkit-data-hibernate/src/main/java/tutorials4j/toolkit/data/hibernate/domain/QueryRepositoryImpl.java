package tutorials4j.toolkit.data.hibernate.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class QueryRepositoryImpl<E extends Entity, ID extends Serializable>
    extends SimpleJpaRepository<E, ID> implements QueryRepository<E> {
  private final EntityManager em;

  public QueryRepositoryImpl(JpaEntityInformation<E, ID> info, EntityManager em) {
    super(info, em);
    this.em = em;
  }

  @Override
  public List<E> findAll(BaseQuery<E> query) {
    Assert.notNull(query, "query must not be null");
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<E> cq = cb.createQuery(getDomainClass());
    Root<E> root = cq.from(getDomainClass());

    Predicate p = query.buildSpecification().toPredicate(root, cq, cb);
    if (p != null) cq.where(p);

    TypedQuery<E> q = em.createQuery(cq);
    int limit = query.getMaxQueryLimit();
    if (limit > 0) {
      q.setMaxResults(limit + 1);
    } else {
      log.debug("Query查询没有限制记录数，确认查询条件，避免返回全表数据");
    }

    List<E> dataList = q.getResultList();
    if (limit == DefaultConsts.MAX_JPA_FIND_COUNT_BY_QUERY && dataList.size() > limit) {
      // 使用默认限制数，超出范围需警告
      log.warn("Query查询限制记录数{}, 但实际查询记录数超出了限制，确认查询参数是否正确", limit);
    }

    if (limit > 0 && dataList.size() > limit) {
      return dataList.subList(0, limit);
    }

    return dataList;
  }

  @Override
  public long count(BaseQuery<E> query) {
    Assert.notNull(query, "query must not be null");
    return super.count(query.buildSpecification());
  }

  @Override
  public Page<E> findAll(BaseQuery<E> query, Pageable pageable) {
    Assert.notNull(query, "query must not be null");
    return super.findAll(query.buildSpecification(), pageable);
  }
}
