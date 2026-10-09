package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link BaseService} 单元测试：接口层次与读写委托。
 *
 * @author Yun Jiao
 */
@SuppressWarnings("unchecked")
class BaseServiceTest {
  static class TestEntity extends BaseEntity<Long> {}

  private final BaseRepository<TestEntity, Long> repository = mock(BaseRepository.class);
  private final BaseService<TestEntity, Long> service = () -> repository;

  @Test
  @DisplayName("继承 WriteableService，进而继承 ReadableService")
  void shouldExtendWriteableAndReadableService() {
    assertThat(WriteableService.class).isAssignableFrom(BaseService.class);
    assertThat(ReadableService.class).isAssignableFrom(WriteableService.class);
    assertThat(ReadableService.class).isAssignableFrom(BaseService.class);
  }

  @Test
  @DisplayName("仅 getRepository 为抽象方法，可直接用 lambda 实现")
  void shouldOnlyDeclareGetRepositoryAsAbstract() {
    List<Method> abstractMethods =
        Arrays.stream(BaseService.class.getMethods())
            .filter(method -> Modifier.isAbstract(method.getModifiers()))
            .toList();

    assertThat(abstractMethods).hasSize(1);
    assertThat(abstractMethods.get(0).getName()).isEqualTo("getRepository");
  }

  @Test
  @DisplayName("读与写操作都委托给同一个 Repository")
  void shouldDelegateReadAndWriteOperations() {
    TestEntity entity = new TestEntity();
    when(repository.save(entity)).thenReturn(entity);
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    when(repository.count()).thenReturn(1L);

    assertThat(service.save(entity)).isSameAs(entity);
    assertThat(service.findById(1L)).isSameAs(entity);
    assertThat(service.count()).isEqualTo(1L);

    verify(repository).save(entity);
    verify(repository).findById(1L);
    verify(repository).count();
  }

  @Test
  @DisplayName("getRepository 返回底层 Repository 实例")
  void shouldExposeRepository() {
    assertThat(service.getRepository()).isSameAs(repository);
  }
}
