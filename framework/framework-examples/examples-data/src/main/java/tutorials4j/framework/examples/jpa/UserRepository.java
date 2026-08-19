package tutorials4j.framework.examples.jpa;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 用户数据仓库接口。
 *
 * <p>继承 {@link BaseRepository}，提供用户实体的基础数据访问能力。
 *
 * @author Yun Jiao
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {}
