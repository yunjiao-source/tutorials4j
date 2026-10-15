package tutorials4j.toolkit.core.constant;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface PropertyConsts {
  /** toolkit */
  String PROPERTY_TUTORIALS4J = "tutorials4j";

  String PROPERTY_TOOLKIT = PROPERTY_TUTORIALS4J + ".toolkit";

  String PROPERTY_TOOLKIT_IP2REGION = PROPERTY_TOOLKIT + ".ip2region";

  String PROPERTY_TOOLKIT_XXL_JOB = PROPERTY_TOOLKIT + ".xxl-job";

  /** security */
  String PROPERTY_TOOLKIT_SECURITY = PROPERTY_TOOLKIT + ".security";

  String PROPERTY_TOOLKIT_SECURITY_SA_TOKEN = PROPERTY_TOOLKIT_SECURITY + ".sa-token";

  /** data */
  String PROPERTY_TOOLKIT_DATA = PROPERTY_TOOLKIT + ".data";

  String PROPERTY_TOOLKIT_DATA_MYBATIS_PLUS = PROPERTY_TOOLKIT_DATA + ".mybatis-plus";

  /** cache */
  String PROPERTY_TOOLKIT_CACHE = PROPERTY_TOOLKIT + ".cache";

  String PROPERTY_TOOLKIT_CACHE_CAFFEINE = PROPERTY_TOOLKIT_CACHE + ".caffeine";
  String PROPERTY_TOOLKIT_CACHE_REDIS = PROPERTY_TOOLKIT_CACHE + ".redis";

  /** feature */
  String PROPERTY_FEATURE = PROPERTY_TUTORIALS4J + ".feature";

  String PROPERTY_FEATURE_OAUTH = PROPERTY_FEATURE + ".oauth";

  /** microservice */
  String PROPERTY_MICROSERVICE = PROPERTY_TUTORIALS4J + ".microservice";

  String PROPERTY_MICROSERVICE_OAUTH = PROPERTY_MICROSERVICE + ".oauth";
}
