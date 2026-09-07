package tutorials4j.framework.feature.oss.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.oss.component.FileStorageSpringEntityConvert;
import tutorials4j.framework.feature.oss.model.FileDetailEntity;
import tutorials4j.framework.feature.oss.model.FileDetailQuery;
import tutorials4j.framework.feature.oss.service.FileDetailService;
import tutorials4j.framework.feature.oss.service.FilePartDetailRepository;

/**
 * 文件查询端点，提供文件详情、分片信息及分页查询接口。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/file-storage-spring/query")
@RequiredArgsConstructor
public class QueryEndpoint {

  private final FileDetailService fileDetailService;
  private final FilePartDetailRepository filePartDetailRepository;
  private final FileStorageSpringEntityConvert fileStorageSpringEntityConvert;

  /**
   * 根据文件ID获取文件信息（转换为FileInfo对象）。
   *
   * @param id 文件ID
   * @return 文件信息
   */
  @GetMapping("{id}")
  public Result<FileInfo> fileInfo(@PathVariable("id") String id) {
    FileDetailEntity entity = fileDetailService.findById(id);
    return Result.success(fileStorageSpringEntityConvert.convert(entity));
  }

  /**
   * 根据文件ID获取该文件的所有分片信息（用于手动分片上传场景）。
   *
   * @param id 文件ID
   * @return 分片信息列表
   */
  @GetMapping("{id}/part")
  public Result<List<FilePartInfo>> filePartInfos(@PathVariable("id") String id) {
    FileDetailEntity entity = fileDetailService.findById(id);
    List<FilePartInfo> filePartInfos =
        filePartDetailRepository.findByUploadId(entity.getUploadId()).stream()
            .map(fileStorageSpringEntityConvert::convert)
            .toList();
    return Result.success(filePartInfos);
  }

  /**
   * 分页查询文件列表，支持按文件名、平台、对象ID/类型、创建时间范围过滤。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 分页结果，其中内容为转换后的FileInfo对象
   */
  @GetMapping("page")
  public Result<PagedModel<FileInfo>> findPage(FileDetailQuery query, Pageable pageable) {
    Page<FileDetailEntity> page = fileDetailService.findByPage(query, pageable);
    return Result.success(new PagedModel<>(page.map(fileStorageSpringEntityConvert::convert)));
  }
}
