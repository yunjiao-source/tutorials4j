package tutorials4j.framework.oss.fss.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.InputStream;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.UploadPretreatment;
import org.dromara.x.file.storage.core.aspect.AbortMultipartUploadAspectChain;
import org.dromara.x.file.storage.core.aspect.CompleteMultipartUploadAspectChain;
import org.dromara.x.file.storage.core.aspect.DeleteAspectChain;
import org.dromara.x.file.storage.core.aspect.DownloadAspectChain;
import org.dromara.x.file.storage.core.aspect.FileStorageAspect;
import org.dromara.x.file.storage.core.aspect.InitiateMultipartUploadAspectChain;
import org.dromara.x.file.storage.core.aspect.UploadAspectChain;
import org.dromara.x.file.storage.core.aspect.UploadPartAspectChain;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.tika.ContentTypeDetect;
import org.dromara.x.file.storage.core.upload.AbortMultipartUploadPretreatment;
import org.dromara.x.file.storage.core.upload.CompleteMultipartUploadPretreatment;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.dromara.x.file.storage.core.upload.InitiateMultipartUploadPretreatment;
import org.dromara.x.file.storage.core.upload.UploadPartPretreatment;
import org.springframework.util.StringUtils;

/**
 * 文件存储指标切面。
 *
 * <p>实现 {@link FileStorageAspect} 接口，拦截文件操作并采集指标：
 *
 * <ul>
 *   <li>记录成功/失败次数、文件大小、耗时
 *   <li>维护活跃任务数（Gauge）
 *   <li>操作失败时捕获异常并记录错误类型
 * </ul>
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class MetricsFileStorageAspect implements FileStorageAspect {
  private final MeterRegistry registry;
  private final FileStorageMetricsCollector collector;

  private String getPlatform(FileStorage fileStorage) {
    if (fileStorage == null) return "unknown";
    return fileStorage.getPlatform();
  }

  private String getExtension(FileInfo fileInfo) {
    if (fileInfo == null) return null;
    return StringUtils.hasText(fileInfo.getExt()) ? fileInfo.getExt() : null;
  }

  private long getSize(FileInfo fileInfo) {
    return fileInfo != null ? fileInfo.getSize() : 0L;
  }

  @Override
  public FileInfo uploadAround(
      UploadAspectChain chain,
      FileInfo fileInfo,
      UploadPretreatment pre,
      FileStorage fileStorage,
      FileRecorder fileRecorder) {
    String operation = "upload";
    String platform = getPlatform(fileStorage);
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    collector.activeTaskIncrement(operation, platform);
    try {
      FileInfo result = chain.next(fileInfo, pre, fileStorage, fileRecorder);
      if (result != null) {
        collector.recordSuccess(operation, platform, ext, result.getSize(), sample);
      } else {
        collector.recordFailure(
            operation, platform, ext, new RuntimeException("Upload returned null"), sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    } finally {
      collector.activeTaskDecrement(operation, platform);
    }
  }

  @Override
  public FileInfo initiateMultipartUploadAround(
      InitiateMultipartUploadAspectChain chain,
      FileInfo fileInfo,
      InitiateMultipartUploadPretreatment pre,
      FileStorage fileStorage,
      FileRecorder fileRecorder) {
    String operation = "initiateMultipartUpload";
    String platform = getPlatform(fileStorage);
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    try {
      FileInfo result = chain.next(fileInfo, pre, fileStorage, fileRecorder);
      if (result != null) {
        collector.recordSuccess(operation, platform, ext, result.getSize(), sample);
      } else {
        collector.recordFailure(
            operation,
            platform,
            ext,
            new RuntimeException("Initiate multipart upload returned null"),
            sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    }
  }

  @Override
  public FilePartInfo uploadPart(
      UploadPartAspectChain chain,
      UploadPartPretreatment pre,
      FileStorage fileStorage,
      FileRecorder fileRecorder) {
    String operation = "uploadPart";
    String platform = getPlatform(fileStorage);
    // 分片预处理的 fileInfo 可能为空
    FileInfo fileInfo = pre.getFileInfo();
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    try {
      FilePartInfo result = chain.next(pre, fileStorage, fileRecorder);
      if (result != null) {
        // 记录成功，大小取实际分片大小
        collector.recordSuccess(operation, platform, ext, result.getPartSize(), sample);
      } else {
        collector.recordFailure(
            operation, platform, ext, new RuntimeException("Upload part returned null"), sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    }
  }

  @Override
  public FileInfo completeMultipartUploadAround(
      CompleteMultipartUploadAspectChain chain,
      CompleteMultipartUploadPretreatment pre,
      FileStorage fileStorage,
      FileRecorder fileRecorder,
      ContentTypeDetect contentTypeDetect) {
    String operation = "completeMultipartUpload";
    String platform = getPlatform(fileStorage);
    FileInfo fileInfo = pre.getFileInfo();
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    try {
      FileInfo result = chain.next(pre, fileStorage, fileRecorder, contentTypeDetect);
      if (result != null) {
        collector.recordSuccess(operation, platform, ext, result.getSize(), sample);
      } else {
        collector.recordFailure(
            operation,
            platform,
            ext,
            new RuntimeException("Complete multipart upload returned null"),
            sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    }
  }

  @Override
  public FileInfo abortMultipartUploadAround(
      AbortMultipartUploadAspectChain chain,
      AbortMultipartUploadPretreatment pre,
      FileStorage fileStorage,
      FileRecorder fileRecorder) {
    String operation = "abortMultipartUpload";
    String platform = getPlatform(fileStorage);
    FileInfo fileInfo = pre.getFileInfo();
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    try {
      FileInfo result = chain.next(pre, fileStorage, fileRecorder);
      if (result != null) {
        collector.recordSuccess(operation, platform, ext, 0, sample); // 取消无文件大小
      } else {
        collector.recordFailure(
            operation,
            platform,
            ext,
            new RuntimeException("Abort multipart upload returned null"),
            sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    }
  }

  @Override
  public boolean deleteAround(
      DeleteAspectChain chain,
      FileInfo fileInfo,
      FileStorage fileStorage,
      FileRecorder fileRecorder) {
    String operation = "delete";
    String platform = getPlatform(fileStorage);
    String ext = getExtension(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    try {
      boolean result = chain.next(fileInfo, fileStorage, fileRecorder);
      if (result) {
        collector.recordSuccess(operation, platform, ext, fileInfo.getSize(), sample);
      } else {
        collector.recordFailure(
            operation, platform, ext, new RuntimeException("Delete returned false"), sample);
      }
      return result;
    } catch (Exception e) {
      collector.recordFailure(operation, platform, ext, e, sample);
      throw e;
    }
  }

  @Override
  public void downloadAround(
      DownloadAspectChain chain,
      FileInfo fileInfo,
      FileStorage fileStorage,
      Consumer<InputStream> consumer) {
    String operation = "download";
    String platform = getPlatform(fileStorage);
    String ext = getExtension(fileInfo);
    long size = getSize(fileInfo);
    Timer.Sample sample = Timer.start(registry);
    collector.activeTaskIncrement(operation, platform);

    // 包装 consumer 以在流消费后记录
    Consumer<InputStream> wrapped =
        is -> {
          try {
            consumer.accept(is);
            collector.recordSuccess(operation, platform, ext, size, sample);
          } catch (Exception e) {
            collector.recordFailure(operation, platform, ext, e, sample);
            throw e;
          } finally {
            collector.activeTaskDecrement(operation, platform);
          }
        };

    try {
      chain.next(fileInfo, fileStorage, wrapped);
    } catch (Exception e) {
      // 如果链调用本身抛出异常（非consumer内），也记录
      collector.recordFailure(operation, platform, ext, e, sample);
      collector.activeTaskDecrement(operation, platform);
      throw e;
    }
  }
}
