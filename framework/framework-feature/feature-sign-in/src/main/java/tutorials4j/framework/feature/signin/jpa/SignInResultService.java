package tutorials4j.framework.feature.signin.jpa;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class SignInResultService implements BaseService<SignInResultEntity, Long> {
  private final SignInResultRepository signInResultRepository;

  @Override
  public BaseRepository<SignInResultEntity, Long> getRepository() {
    return signInResultRepository;
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<SignInResultEntity> find(SignInResultQuery query, Pageable pageable) {
    return signInResultRepository.findAll(query.buildSpecification(), pageable);
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<SignInResultEntity> find(SignInResultQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
