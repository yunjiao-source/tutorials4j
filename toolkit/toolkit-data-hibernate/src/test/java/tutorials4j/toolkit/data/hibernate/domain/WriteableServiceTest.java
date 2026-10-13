package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link WriteableService} 单元测试：默认写操作到 Repository 的委托。
 *
 * @author YunJiao Yang
 */
@SuppressWarnings("unchecked")
class WriteableServiceTest {
  static class TestEntity extends BaseEntity<Long> {}

  private final BaseRepository<TestEntity, Long> repository = mock(BaseRepository.class);
  private final WriteableService<TestEntity, Long> service = () -> repository;

  @Test
  @DisplayName("save 委托给 Repository 并返回保存结果")
  void shouldSave() {
    TestEntity entity = new TestEntity();
    when(repository.save(entity)).thenReturn(entity);

    assertThat(service.save(entity)).isSameAs(entity);

    verify(repository).save(entity);
  }

  @Test
  @DisplayName("saveAll 委托给 Repository")
  void shouldSaveAll() {
    List<TestEntity> entities = List.of(new TestEntity(), new TestEntity());
    when(repository.saveAll(entities)).thenReturn(entities);

    assertThat(service.saveAll(entities)).isSameAs(entities);

    verify(repository).saveAll(entities);
  }

  @Test
  @DisplayName("saveAndFlush 委托给 Repository")
  void shouldSaveAndFlush() {
    TestEntity entity = new TestEntity();
    when(repository.saveAndFlush(entity)).thenReturn(entity);

    assertThat(service.saveAndFlush(entity)).isSameAs(entity);

    verify(repository).saveAndFlush(entity);
  }

  @Test
  @DisplayName("saveAllAndFlush 委托给 Repository")
  void shouldSaveAllAndFlush() {
    List<TestEntity> entities = List.of(new TestEntity());
    when(repository.saveAllAndFlush(entities)).thenReturn(entities);

    assertThat(service.saveAllAndFlush(entities)).isSameAs(entities);

    verify(repository).saveAllAndFlush(entities);
  }

  @Test
  @DisplayName("delete 相关方法委托给 Repository")
  void shouldDelegateDeleteOperations() {
    TestEntity entity = new TestEntity();
    List<TestEntity> entities = List.of(entity);

    service.delete(entity);
    service.deleteAll(entities);
    service.deleteAll();
    service.deleteAllInBatch();
    service.deleteById(1L);

    verify(repository).delete(entity);
    verify(repository).deleteAll(entities);
    verify(repository).deleteAll();
    verify(repository).deleteAllInBatch();
    verify(repository).deleteById(1L);
  }

  @Test
  @DisplayName("flush 委托给 Repository")
  void shouldFlush() {
    service.flush();

    verify(repository).flush();
  }

  @Test
  @DisplayName("未调用任何方法时不与 Repository 交互")
  void shouldNotInteractWithoutInvocation() {
    verify(repository, never()).save(any());
    verify(repository, never()).deleteAll();
  }
}
