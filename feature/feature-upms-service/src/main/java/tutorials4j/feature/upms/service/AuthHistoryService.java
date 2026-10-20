package tutorials4j.feature.upms.service;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.feature.core.event.local.AuthEvent;
import tutorials4j.feature.upms.autoconfigure.UpmsFeatureProperties;
import tutorials4j.feature.upms.autoconfigure.UpmsFeatureProperties.ClearAuthHistoryOptions;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.feature.upms.repository.AuthHistoryRepository;
import tutorials4j.feature.upms.repository.UserRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;
import tutorials4j.toolkit.data.hibernate.domain.StringIdOnly;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Service
@RequiredArgsConstructor
public class AuthHistoryService implements BaseService<AuthHistoryEntity, String> {
  private final AuthHistoryRepository authHistoryRepository;
  private final UserRepository userRepository;
  private final UpmsFeatureProperties properties;

  @Override
  public BaseRepository<AuthHistoryEntity, String> getRepository() {
    return authHistoryRepository;
  }

  public Page<AuthHistoryEntity> findByPage(AuthHistoryQuery query, Pageable pageable) {
    return authHistoryRepository.findAll(query.buildSpecification(), pageable);
  }

  @Transactional(rollbackFor = Exception.class)
  public void createBy(AuthEvent authEvent) {
    String userId = authEvent.getUserId();
    userRepository
        .findById(userId)
        .ifPresent(
            entity -> {
              AuthHistoryEntity authHistoryEntity = new AuthHistoryEntity();
              authHistoryEntity.setUser(entity);
              authHistoryEntity.setAuthStatus(authEvent.getStatus());
              authHistoryEntity.setCreatedTime(Instant.now());
              authHistoryRepository.save(authHistoryEntity);
            });
  }

  @Transactional(rollbackFor = Exception.class)
  public int clearExpireData() {
    ClearAuthHistoryOptions options = properties.getClearAuthHistory();
    Instant createTimeEnd = Instant.now().minus(options.getDataExpired());
    AuthHistoryQuery query = new AuthHistoryQuery();
    query.setCreatedTimeEnd(createTimeEnd);

    Pageable pageable =
        PageRequest.of(0, options.getMaxSizePreLoop(), Sort.by("createdTime").descending());

    Page<StringIdOnly> page =
        authHistoryRepository.findBy(
            query.buildSpecification(), q -> q.as(StringIdOnly.class).project("id").page(pageable));

    Objects.requireNonNull(page, "page must not be null");
    Set<String> ids =
        page.getContent().stream().map(StringIdOnly::getId).collect(Collectors.toSet());
    if (!ids.isEmpty()) {
      authHistoryRepository.deleteAllByIdInBatch(ids);
    }
    return ids.size();
  }
}
