package tutorials4j.framework.feature.oss.fss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.core.exception.FeatureErrorCode;
import tutorials4j.framework.oss.core.autoconfigure.OssProperties;
import tutorials4j.framework.oss.fss.component.SimpleProgressListener;

/**
 * 文件上传端点，支持单文件上传，并自动生成缩略图（针对图片类型）。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/api/fss/upload")
@RequiredArgsConstructor
public class UploadEndpoint {

  private final FileStorageService fileStorageService;
  private final OssProperties properties;

  /**
   * 单文件上传接口。
   *
   * @param file 上传的文件
   * @param params 可选参数
   * @return 上传成功后的文件信息
   */
  @PostMapping("/single")
  public Result<FileInfo> upload(MultipartFile file, @ModelAttribute UploadParams params) {

    // 构建上传请求，设置路径（随机生成目录）、平台、对象关联、保存文件名、进度监听
    FileInfo fileInfo =
        fileStorageService
            .of(file)
            .setPath(properties.getRandomFolder().randomPath("/")) // 随机生成目录
            .setPlatform(StringUtils.isNotBlank(params.platform()), params.platform())
            .setObjectId(StringUtils.isNotBlank(params.objectId()), params.objectId())
            .setObjectType(StringUtils.isNotBlank(params.objectType()), params.objectType())
            .setSaveFilename(StringUtils.isNotBlank(params.saveFilename()), params.saveFilename())
            .setProgressListener(log.isDebugEnabled(), SimpleProgressListener.uploadListener())
            .thumbnail(
                Boolean.TRUE.equals(params.isImageType()),
                th -> th.size(200, 200)) // 图片类型生成200x200缩略图
            .upload();

    assertFile(fileInfo);
    return Result.success(fileInfo);
  }

  /**
   * 校验文件上传结果，若失败则抛出异常。
   *
   * @param fileInfo 上传结果
   */
  private void assertFile(FileInfo fileInfo) {
    if (fileInfo == null) {
      throw FeatureErrorCode.FILE_UPLOAD_OSS_FAIL.throwed();
    }
  }
}
