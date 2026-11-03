package tutorials4j.toolkit.data.hibernate.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA)
public class HibernateDataToolkitProperties {
  private String[] repositoryBasePackages;
  private String[] entityBasePackages;
}
