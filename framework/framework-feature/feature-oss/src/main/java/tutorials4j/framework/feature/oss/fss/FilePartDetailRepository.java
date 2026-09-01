package tutorials4j.framework.feature.oss.fss;

import java.util.List;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 文件分片详情数据访问层。
 *
 * @author Yun Jiao
 */
@Repository
public interface FilePartDetailRepository extends BaseRepository<FilePartDetailEntity, String> {

  /**
   * 根据上传ID查询所有分片记录。
   *
   * @param uploadId 上传ID
   * @return 分片列表
   */
  List<FilePartDetailEntity> findByUploadId(String uploadId);
}
