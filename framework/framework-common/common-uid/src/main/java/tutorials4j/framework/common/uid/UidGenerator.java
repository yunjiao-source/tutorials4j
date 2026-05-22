package tutorials4j.framework.common.uid;

import cc.siyecao.uid.core.impl.DefaultUidGenerator;
import tutorials4j.framework.common.spring.core.SnowflakeIdProvider;
import tutorials4j.framework.common.spring.properties.UidProperties;

/**
 * 框架统一的 UID 生成器接口。
 *
 * <p>定义全局唯一 ID 的基本操作：生成 long 型 ID、生成字符串 ID、解析 ID 组成以及销毁资源。
 *
 * <p>框架提供了两种实现：
 *
 * <ul>
 *   <li>{@link UidDefaultedGenerator} —— 实时计算，简单可靠
 *   <li>{@link UidCachedGenerator} —— 缓存预取，适合超高并发
 * </ul>
 *
 * @author Yun Jiao
 */
public interface UidGenerator {
  /** 生成下一个唯一ID（长整型）。 */
  long nextUid();

  /** 生成下一个唯一ID的字符串形式。 */
  String nextUidStr();

  String parseUid(long uid);

  /** 释放生成器占用的资源（应用关闭时调用）。 */
  void destroy();

  default void fill(UidProperties properties, DefaultUidGenerator defaultUidGenerator) {
    defaultUidGenerator.setEpochStr(properties.getEpochStr());
    defaultUidGenerator.setSeqBits(properties.getSeqBits());
    defaultUidGenerator.setTimeBits(properties.getTimeBits());
    defaultUidGenerator.setWorkerBits(properties.getWorkerBits());
    defaultUidGenerator.setWorkerIdAssigner(SnowflakeIdProvider.instance::provideWorkerId);
  }
}
