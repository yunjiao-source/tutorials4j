package tutorials4j.framework.feature.oss.fss;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

/**
 * 文件详情业务服务层。
 *
 * <p>继承{@link BaseService}获得基础CRUD能力，并扩展分页查询方法。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class FileDetailService implements BaseService<FileDetailEntity, String> {

  private final FileDetailRepository fileDetailRepository;

  @Override
  public BaseRepository<FileDetailEntity, String> getRepository() {
    return fileDetailRepository;
  }

  /**
   * 根据查询条件和分页参数进行分页查询。
   *
   * @param query 查询条件
   * @param pageable 分页信息
   * @return 文件实体分页结果
   */
  public Page<FileDetailEntity> find(FileDetailQuery query, Pageable pageable) {
    return fileDetailRepository.findAll(query.buildSpecification(), pageable);
  }
}
