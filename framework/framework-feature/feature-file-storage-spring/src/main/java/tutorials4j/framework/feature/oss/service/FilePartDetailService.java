package tutorials4j.framework.feature.oss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;
import tutorials4j.framework.feature.oss.model.FilePartDetailEntity;

/**
 * 文件分片详情业务服务层。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class FilePartDetailService implements BaseService<FilePartDetailEntity, String> {

  private final FilePartDetailRepository filePartDetailRepository;

  @Override
  public BaseRepository<FilePartDetailEntity, String> getRepository() {
    return filePartDetailRepository;
  }
}
