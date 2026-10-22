package tutorials4j.toolkit.data.hibernate.component;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import tutorials4j.toolkit.core.constant.SymbolConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SuffixUnderscoreNamingStrategy implements PhysicalNamingStrategy {
  // 持有默认策略，保留 Spring Boot 原有的驼峰转下划线等行为
  private final PhysicalNamingStrategySnakeCaseImpl delegate =
      new PhysicalNamingStrategySnakeCaseImpl();

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
    return delegate.toPhysicalCatalogName(name, context);
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
    return delegate.toPhysicalSchemaName(name, context);
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
    return delegate.toPhysicalTableName(name, context);
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
    return delegate.toPhysicalSequenceName(name, context);
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
    Identifier defaultName = delegate.toPhysicalColumnName(name, context);
    return Identifier.toIdentifier(defaultName.getText() + SymbolConsts.UNDERLINE);
  }
}
