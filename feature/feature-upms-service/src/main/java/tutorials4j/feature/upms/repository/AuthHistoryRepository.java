package tutorials4j.feature.upms.repository;

import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface AuthHistoryRepository extends BaseRepository<AuthHistoryEntity, String> {}
