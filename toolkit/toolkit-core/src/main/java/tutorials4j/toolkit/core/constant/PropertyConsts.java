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

  String PROPERTY_TOOLKIT_DATA_JPA = PROPERTY_TOOLKIT_DATA + ".jpa";

  String PROPERTY_TOOLKIT_DATA_JPA_REPOSITORY_BASE_PACKAGES =
      PROPERTY_TOOLKIT_DATA_JPA + ".repository-base-packages";

  String PROPERTY_TOOLKIT_DATA_JPA_ENTITY_BASE_PACKAGES =
      PROPERTY_TOOLKIT_DATA_JPA + ".entity-base-packages";

  /** cache */
  String PROPERTY_TOOLKIT_CACHE = PROPERTY_TOOLKIT + ".cache";

  String PROPERTY_TOOLKIT_CACHE_CAFFEINE = PROPERTY_TOOLKIT_CACHE + ".caffeine";
  String PROPERTY_TOOLKIT_CACHE_REDIS = PROPERTY_TOOLKIT_CACHE + ".redis";

  /** feature */
  String PROPERTY_FEATURE = PROPERTY_TUTORIALS4J + ".feature";

  String PROPERTY_FEATURE_UPMS = PROPERTY_FEATURE + ".upms";

  /** service */
  String PROPERTY_SERVICE = PROPERTY_TUTORIALS4J + ".service";

  String PROPERTY_SERVICE_UPMS = PROPERTY_SERVICE + ".upms";
}
