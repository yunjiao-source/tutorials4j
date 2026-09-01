package tutorials4j.framework.feature.oss.fss;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于数据库的{@link FileRecorder}实现。
 *
 * <p>用于记录文件上传、更新、删除以及分片上传信息的持久化，支持事务。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class SimpleDatabaseFileRecorder implements FileRecorder {

  private final FileDetailService fileDetailService;
  private final FileDetailRepository fileDetailRepository;
  private final FilePartDetailRepository filePartDetailRepository;
  private final FileStorageSpringEntityConvert fileStorageSpringEntityConvert;

  /**
   * 保存文件信息（首次上传完成时调用）。
   *
   * @param fileInfo 文件信息
   * @return 是否保存成功
   */
  @Override
  public boolean save(FileInfo fileInfo) {
    FileDetailEntity entity = fileStorageSpringEntityConvert.convert(fileInfo);
    fileDetailService.save(entity);
    fileInfo.setId(entity.getId()); // 将生成的ID回填
    return true;
  }

  /**
   * 更新文件信息（如上传完成、缩略图生成等）。
   *
   * @param fileInfo 文件信息
   */
  @Override
  public void update(FileInfo fileInfo) {
    FileDetailEntity entity = fileStorageSpringEntityConvert.convert(fileInfo);
    fileDetailService.save(entity);
  }

  /**
   * 根据URL获取文件信息。
   *
   * @param url 文件访问地址
   * @return 文件信息，若不存在返回null
   */
  @Override
  public FileInfo getByUrl(String url) {
    return fileDetailRepository
        .findByUrl(url)
        .map(fileStorageSpringEntityConvert::convert)
        .orElse(null);
  }

  /**
   * 根据URL删除文件记录（实际删除数据库记录）。
   *
   * @param url 文件访问地址
   * @return 是否删除成功
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delete(String url) {
    Optional<FileDetailEntity> entityOptional = fileDetailRepository.findByUrl(url);
    if (entityOptional.isPresent()) {
      fileDetailRepository.deleteById(entityOptional.get().getId());
      return true;
    }
    return false;
  }

  /**
   * 保存分片信息。
   *
   * @param filePartInfo 分片信息
   */
  @Override
  public void saveFilePart(FilePartInfo filePartInfo) {
    FilePartDetailEntity entity = fileStorageSpringEntityConvert.convert(filePartInfo);
    filePartDetailRepository.save(entity);
    filePartInfo.setId(entity.getId());
  }

  /**
   * 根据上传ID删除所有分片记录（用于取消上传或完成合并后清理）。
   *
   * @param uploadId 上传ID
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteFilePartByUploadId(String uploadId) {
    List<FilePartDetailEntity> entities = filePartDetailRepository.findByUploadId(uploadId);
    filePartDetailRepository.deleteAllInBatch(entities);
  }
}
