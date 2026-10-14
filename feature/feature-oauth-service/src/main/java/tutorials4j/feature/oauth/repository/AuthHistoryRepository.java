package tutorials4j.feature.oauth.repository;

import org.springframework.stereotype.Repository;
import tutorials4j.feature.oauth.entity.AuthHistoryEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface AuthHistoryRepository extends BaseRepository<AuthHistoryEntity, String> {}
