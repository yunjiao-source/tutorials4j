package tutorials4j.framework.examples.jpa;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {}
