package tutorials4j.toolkit.ip2region.component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.InetAddressException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import tutorials4j.toolkit.core.util.ResourceUtils;
import tutorials4j.toolkit.ip2region.autoconfigure.Ip2RegionToolkitProperties;
import tutorials4j.toolkit.ip2region.autoconfigure.Ip2RegionToolkitProperties.Ip2RegionOptions;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@RequiredArgsConstructor
public class Ip2RegionSearcher implements InitializingBean, DisposableBean {
  private final Ip2RegionToolkitProperties properties;
  private final Object initLock = new Object();
  private volatile Ip2Region ip2Region;
  private final AtomicBoolean closed = new AtomicBoolean(false);

  public String search(String ip) {
    if (ip == null || ip.trim().isEmpty()) {
      return null;
    }
    try {
      return getIp2Region().search(ip);
    } catch (InetAddressException | IOException | InterruptedException e) {
      log.error("ip2Region 查询异常", e);
    }

    return null;
  }

  @Override
  public void destroy() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }

    final Ip2Region local = this.ip2Region;
    if (local == null) {
      return;
    }

    final Duration closeTimeout = properties.getCloseTimeout();
    final long timeoutMs = closeTimeout == null ? 0L : closeTimeout.toMillis();

    try {
      local.close(timeoutMs);
      if (log.isDebugEnabled()) {
        log.debug("ip2Region 已关闭");
      }
    } catch (Exception e) {
      log.warn("关闭 ip2Region 异常", e);
    } finally {
      this.ip2Region = null;
    }
  }

  private Ip2Region getIp2Region() {
    Ip2Region local = this.ip2Region;
    if (local != null) {
      return local;
    }

    if (closed.get()) {
      throw new IllegalStateException("Ip2Region 已关闭，无法再初始化");
    }

    synchronized (initLock) {
      // double check
      local = this.ip2Region;
      if (local != null) {
        return local;
      }
      if (closed.get()) {
        throw new IllegalStateException("Ip2Region 已关闭，无法再初始化");
      }

      local = doInit();
      this.ip2Region = local;
      return local;
    }
  }

  private Ip2Region doInit() {
    Resource v4Res = ResourceUtils.getInstance().getResource(properties.getV4xdb());
    Resource v6Res = ResourceUtils.getInstance().getResource(properties.getV6xdb());
    long start = System.currentTimeMillis();
    try {
      final Ip2RegionOptions v4Options = properties.getV4();
      final Config v4Config =
          Config.custom()
              .setCachePolicy(v4Options.getCachePolicy().ordinal())
              .setSearchers(v4Options.getSearchers())
              .setCacheSliceBytes(Math.toIntExact(v4Options.getCacheSliceBytes().toBytes()))
              .setXdbFile(v4Res.getFile())
              .setFairLock(v4Options.isFairLock())
              .asV4();

      final Ip2RegionOptions v6Options = properties.getV6();
      final Config v6Config =
          Config.custom()
              .setCachePolicy(v6Options.getCachePolicy().ordinal())
              .setSearchers(v6Options.getSearchers())
              .setCacheSliceBytes(Math.toIntExact(v6Options.getCacheSliceBytes().toBytes()))
              .setXdbFile(v6Res.getFile())
              .setFairLock(v6Options.isFairLock())
              .asV6();

      final Ip2Region instance = Ip2Region.create(v4Config, v6Config);

      if (log.isDebugEnabled()) {
        log.debug(
            "ip2Region 加载完成, v4xdb={}, v6xdb={}, cost={}ms",
            properties.getV4xdb(),
            properties.getV6xdb(),
            System.currentTimeMillis() - start);
      }
      return instance;
    } catch (Exception e) {
      // 初始化失败，抛出运行时异常，并允许后续调用重试
      throw new IllegalStateException(
          "ip2Region 初始化失败: v4xdb=" + properties.getV4xdb() + ", v6xdb=" + properties.getV6xdb(),
          e);
    }
  }

  public void warmUp() {
    getIp2Region();
  }

  public boolean isInitialized() {
    return ip2Region != null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (properties.isWarmUp()) {
      warmUp();
    }
  }
}
