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
 * 签到结果服务，提供签到结果的查询能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class SignInResultService implements BaseService<SignInResultEntity, Long> {
  private final SignInResultRepository signInResultRepository;

  /**
   * 返回当前服务使用的数据访问接口。
   *
   * @return {@link SignInResultRepository} 实例
   */
  @Override
  public BaseRepository<SignInResultEntity, Long> getRepository() {
    return signInResultRepository;
  }

  /**
   * 分页查询签到结果。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 分页结果
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<SignInResultEntity> find(SignInResultQuery query, Pageable pageable) {
    return signInResultRepository.findAll(query.buildSpecification(), pageable);
  }

  /**
   * 查询所有匹配条件的签到结果。
   *
   * @param query 查询条件
   * @return 签到结果列表
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<SignInResultEntity> find(SignInResultQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
