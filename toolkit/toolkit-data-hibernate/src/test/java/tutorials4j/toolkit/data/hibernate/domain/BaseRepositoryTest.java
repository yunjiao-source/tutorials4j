package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import tutorials4j.toolkit.data.domain.Entity;

/**
 * {@link BaseRepository} 单元测试：仓储接口契约。
 *
 * @author Yun Jiao
 */
class BaseRepositoryTest {

  @Test
  @DisplayName("为 @NoRepositoryBean 接口，不会被 Spring Data 直接实例化")
  void shouldBeNoRepositoryBean() {
    assertThat(BaseRepository.class.isInterface()).isTrue();
    assertThat(BaseRepository.class.getAnnotation(NoRepositoryBean.class)).isNotNull();
  }

  @Test
  @DisplayName("继承 JpaRepository 与 JpaSpecificationExecutor")
  void shouldExtendJpaRepositoryInterfaces() {
    assertThat(JpaRepository.class).isAssignableFrom(BaseRepository.class);
    assertThat(JpaSpecificationExecutor.class).isAssignableFrom(BaseRepository.class);
  }

  @Test
  @DisplayName("泛型参数为 <E extends Entity, ID extends Serializable>")
  void shouldDeclareExpectedTypeParameters() {
    TypeVariable<Class<BaseRepository>>[] parameters = BaseRepository.class.getTypeParameters();

    assertThat(parameters).hasSize(2);
    assertThat(parameters[0].getName()).isEqualTo("E");
    assertThat(List.of(parameters[0].getBounds())).containsExactly(Entity.class);
    assertThat(parameters[1].getName()).isEqualTo("ID");
    assertThat(List.of(parameters[1].getBounds())).containsExactly(Serializable.class);
  }

  @Test
  @DisplayName("可作为具体仓储的父接口并带上实体与主键类型")
  void shouldBeExtendableWithConcreteTypes() {
    ParameterizedType type = (ParameterizedType) TestRepository.class.getGenericInterfaces()[0];

    assertThat(type.getRawType()).isEqualTo(BaseRepository.class);
    assertThat(type.getActualTypeArguments()).containsExactly(ConcreteEntity.class, Long.class);
  }

  interface TestRepository extends BaseRepository<ConcreteEntity, Long> {}

  static class ConcreteEntity extends BaseEntity<Long> {}
}
