package tutorials4j.framework.feature.oss.service;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.feature.oss.model.FileDetailEntity;

/**
 * 文件详情数据访问层，继承{@link BaseRepository}。
 *
 * <p>提供基础CRUD及通过URL查询文件记录的方法。
 *
 * @author Yun Jiao
 */
@Repository
public interface FileDetailRepository extends BaseRepository<FileDetailEntity, String> {

  /**
   * 根据文件URL查询记录。
   *
   * @param url 文件访问地址
   * @return 文件实体Optional
   */
  Optional<FileDetailEntity> findByUrl(String url);
}
