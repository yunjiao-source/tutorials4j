package tutorials4j.framework.common.core;

import cn.hutool.core.util.IdUtil;
import lombok.Data;

/**
 * 随机目录生成选项，用于为上传文件生成随机子目录结构。
 *
 * <p>基于UUID生成多级目录，每级子目录字符数可配置。
 *
 * @author Yun Jiao
 */
@Data
public class RandomFolderOptions {

  /** 每级子目录字符个数，默认2 */
  private int charsPerSubDir = 2;

  /** 总共生成子目录级数，默认1 */
  private int totalSubDirs = 1;

  /**
   * 生成随机路径，例如 "a1/b2/"（使用给定的分隔符）。
   *
   * <p>从UUID中去掉连字符后，按配置的字符数和级数截取。
   *
   * @param separator 目录分隔符，如 "/" 或 "\\"
   * @return 生成的随机目录路径（以分隔符结尾）
   */
  public String randomPath(String separator) {
    String uuid = IdUtil.fastSimpleUUID();
    StringBuilder sb = new StringBuilder(uuid.length());
    for (int level = 0; level < totalSubDirs; level++) {
      int start = level * charsPerSubDir;
      sb.append(uuid, start, start + charsPerSubDir);
      sb.append(separator);
    }
    return sb.toString();
  }
}
