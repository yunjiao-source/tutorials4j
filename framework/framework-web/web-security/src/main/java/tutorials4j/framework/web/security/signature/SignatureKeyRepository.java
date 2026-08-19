package tutorials4j.framework.web.security.signature;

/**
 * 签名密钥仓库接口。
 *
 * <p>根据应用标识（appKey）获取对应的密钥（appSecret）。实现类可对接数据库、配置文件或远程服务。
 *
 * <p>该接口为函数式接口，仅包含一个抽象方法，可使用 Lambda 表达式或方法引用实现。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface SignatureKeyRepository {
  /**
   * 根据 appKey 获取对应的密钥。
   *
   * @param key 应用标识（appKey）
   * @return 密钥（appSecret），如果不存在则返回 null
   */
  String getSecretKey(String key);
}
