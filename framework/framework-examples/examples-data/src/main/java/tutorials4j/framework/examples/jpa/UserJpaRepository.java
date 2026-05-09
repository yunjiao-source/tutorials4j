package tutorials4j.framework.examples.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 仓库
 *
 * @author Yun Jiao
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    // 分页模糊查询（按姓名）
    Page<User> findByNameContaining(String name, Pageable pageable);
}