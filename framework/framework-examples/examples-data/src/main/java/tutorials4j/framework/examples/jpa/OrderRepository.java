package tutorials4j.framework.examples.jpa;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 订单数据仓库接口。
 *
 * <p>继承 {@link BaseRepository}，提供订单实体的基础数据访问能力。
 *
 * @author Yun Jiao
 */
@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {}
