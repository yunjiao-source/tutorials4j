package tutorials4j.framework.feature.oss.fss;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.oss.fss.component.SimpleProgressListener;

/**
 * 文件下载端点。
 *
 * <p>提供通过文件ID下载文件或预览（inline）的功能。 支持设置Content-Disposition为附件下载或内联预览。
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/api/fss/download")
@RequiredArgsConstructor
public class DownloadEndpoint {

  private final FileDetailService fileDetailService;
  private final FileStorageService fileStorageService;
  private final FileStorageSpringEntityConvert fileStorageSpringEntityConvert;

  /**
   * 根据文件ID下载文件。
   *
   * @param id 文件实体ID
   * @param preview 是否预览模式（true：浏览器内联显示；false：附件下载）
   * @return 包含文件字节数据的响应实体，携带正确的Content-Type和Content-Disposition头
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> downloadFile(
      @PathVariable("id") String id, @RequestParam(defaultValue = "false") boolean preview) {
    // 1. 从数据库获取文件记录
    FileDetailEntity entity = fileDetailService.findById(id);
    // 2. 转换为FileInfo对象，用于文件存储服务
    FileInfo fileInfo = fileStorageSpringEntityConvert.convert(entity);
    // 3. 从存储平台下载文件字节（带进度监听，仅在debug级别启用）
    byte[] data =
        fileStorageService
            .download(fileInfo)
            .setProgressListener(log.isDebugEnabled(), SimpleProgressListener.downloadListener())
            .bytes();

    // 4. 设置响应头
    HttpHeaders headers = new HttpHeaders();
    MediaType mediaType = MediaType.parseMediaType(fileInfo.getContentType());
    MediaType utf8MediaType = new MediaType(mediaType, StandardCharsets.UTF_8);
    headers.setContentType(utf8MediaType);

    String filename = fileInfo.getOriginalFilename();
    ContentDisposition disposition;
    if (preview) {
      // 预览模式：inline，浏览器尝试直接渲染
      disposition =
          ContentDisposition.builder("inline").filename(filename, StandardCharsets.UTF_8).build();
    } else {
      // 下载模式：attachment，强制下载
      disposition =
          ContentDisposition.builder("attachment")
              .filename(filename, StandardCharsets.UTF_8)
              .build();
    }
    headers.setContentDisposition(disposition);

    return ResponseEntity.ok().headers(headers).body(data);
  }
}
