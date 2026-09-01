package tutorials4j.framework.oss.fss.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.ProgressListener;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class SimpleProgressListener {
  private static final String DOWNLOAD = "下载";
  private static final String UPLOAD = "上传";

  public static ProgressListenerImpl uploadListener() {
    return new ProgressListenerImpl(UPLOAD);
  }

  public static ProgressListenerImpl downloadListener() {
    return new ProgressListenerImpl(DOWNLOAD);
  }

  @RequiredArgsConstructor
  public static class ProgressListenerImpl implements ProgressListener {
    private final String prefix;

    @Override
    public void start() {
      log.info(prefix + "进度开始");
    }

    @Override
    public void progress(long progressSize, Long allSize) {
      log.info("已" + prefix + " " + progressSize + " 总大小" + (allSize == null ? "未知" : allSize));
    }

    @Override
    public void finish() {
      log.info(prefix + "结束");
    }
  }
}
