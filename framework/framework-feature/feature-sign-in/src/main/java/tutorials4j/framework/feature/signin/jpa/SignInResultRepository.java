package tutorials4j.framework.feature.signin.jpa;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 签到结果数据访问接口。
 *
 * @author Yun Jiao
 */
@Repository
public interface SignInResultRepository extends BaseRepository<SignInResultEntity, Long> {}
