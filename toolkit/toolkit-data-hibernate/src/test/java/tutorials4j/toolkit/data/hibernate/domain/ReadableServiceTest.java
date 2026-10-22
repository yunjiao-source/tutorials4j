package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.data.exception.DataErrorCode;

/**
 * {@link ReadableService} 单元测试：默认方法到 Repository 的委托与分页参数组装。
 *
 * @author YunJiao Yang
 */
@SuppressWarnings("unchecked")
class ReadableServiceTest {
  static class TestEntity extends BaseEntity<Long> {}

  private final BaseRepository<TestEntity, Long> repository = mock(BaseRepository.class);
  private final ReadableService<TestEntity, Long> service = () -> repository;

  private static Specification<TestEntity> specification() {
    return mock(Specification.class);
  }

  private static Example<TestEntity> example() {
    return mock(Example.class);
  }

  @Test
  @DisplayName("findById 命中时返回实体")
  void shouldFindById() {
    TestEntity expected = new TestEntity();
    when(repository.findById(1L)).thenReturn(Optional.of(expected));

    assertThat(service.findById(1L)).isSameAs(expected);
  }

  @Test
  @DisplayName("findById 未命中时抛出带错误码与主键参数的异常")
  void shouldThrowWhenEntityNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(1L))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("查询实体不存在")
        .satisfies(
            throwable -> {
              ErrorCodeException exception = (ErrorCodeException) throwable;
              assertThat(exception.getErrorCode()).isEqualTo(DataErrorCode.FIND_ENTITY_NOT_EXISTS);
              assertThat(exception.getParams()).containsEntry("id", 1L);
            });
  }

  @Test
  @DisplayName("getReferenceById、existsById、count 委托给 Repository")
  void shouldDelegateSimpleQueries() {
    TestEntity reference = new TestEntity();
    when(repository.getReferenceById(1L)).thenReturn(reference);
    when(repository.existsById(1L)).thenReturn(true);
    when(repository.count()).thenReturn(5L);

    assertThat(service.getReferenceById(1L)).isSameAs(reference);
    assertThat(service.existsById(1L)).isTrue();
    assertThat(service.count()).isEqualTo(5L);
  }

  @Test
  @DisplayName("count(specification) 委托给 Repository")
  void shouldCountBySpecification() {
    Specification<TestEntity> specification = specification();
    when(repository.count(specification)).thenReturn(3L);

    assertThat(service.count(specification)).isEqualTo(3L);

    verify(repository).count(specification);
  }

  @Test
  @DisplayName("findAll 各重载委托给 Repository")
  void shouldDelegateFindAllOverloads() {
    List<TestEntity> entities = List.of(new TestEntity());
    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Specification<TestEntity> specification = specification();
    Example<TestEntity> example = example();

    when(repository.findAll()).thenReturn(entities);
    when(repository.findAll(sort)).thenReturn(entities);
    when(repository.findAll(specification)).thenReturn(entities);
    when(repository.findAll(specification, sort)).thenReturn(entities);
    when(repository.findAll(example)).thenReturn(entities);
    when(repository.findAll(example, sort)).thenReturn(entities);

    assertThat(service.findAll()).isSameAs(entities);
    assertThat(service.findAll(sort)).isSameAs(entities);
    assertThat(service.findAll(specification)).isSameAs(entities);
    assertThat(service.findAll(specification, sort)).isSameAs(entities);
    assertThat(service.findAll(example)).isSameAs(entities);
    assertThat(service.findAll(example, sort)).isSameAs(entities);

    verify(repository).findAll();
    verify(repository).findAll(sort);
    verify(repository).findAll(specification);
    verify(repository).findAll(specification, sort);
    verify(repository).findAll(example);
    verify(repository).findAll(example, sort);
  }

  @Test
  @DisplayName("findByPage(pageable) 直接透传分页参数")
  void shouldFindByPageable() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    when(repository.findAll(pageable)).thenReturn(page);

    assertThat(service.findByPage(pageable)).isSameAs(page);

    verify(repository).findAll(pageable);
  }

  @Test
  @DisplayName("findByPage(pageNumber, pageSize) 组装 PageRequest")
  void shouldFindByPageNumberAndSize() {
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    when(repository.findAll(PageRequest.of(1, 20))).thenReturn(page);

    assertThat(service.findByPage(1, 20)).isSameAs(page);

    verify(repository).findAll(PageRequest.of(1, 20));
  }

  @Test
  @DisplayName("findByPage(pageNumber, pageSize, sort) 组装带排序的 PageRequest")
  void shouldFindByPageNumberSizeAndSort() {
    Sort sort = Sort.by("name");
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    when(repository.findAll(PageRequest.of(0, 5, sort))).thenReturn(page);

    assertThat(service.findByPage(0, 5, sort)).isSameAs(page);

    verify(repository).findAll(PageRequest.of(0, 5, sort));
  }

  @Test
  @DisplayName("findByPage(pageNumber, pageSize, direction, properties) 组装带方向的 PageRequest")
  void shouldFindByPageNumberSizeDirectionAndProperties() {
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    Pageable expected = PageRequest.of(2, 15, Sort.Direction.DESC, "createDate", "id");
    when(repository.findAll(expected)).thenReturn(page);

    assertThat(service.findByPage(2, 15, Sort.Direction.DESC, "createDate", "id")).isSameAs(page);

    verify(repository).findAll(expected);
  }

  @Test
  @DisplayName("现状记录：仅指定排序方向的 findByPage 因缺少排序属性而抛出 IllegalArgumentException")
  void shouldFailWhenOnlyDirectionGiven() {
    // PageRequest.of(page, size, direction) 内部调用 Sort.by(direction, properties)，
    // 而 Spring Data 要求至少一个排序属性，因此该重载在当前实现下永远无法成功。
    // 已知缺陷，暂按现状记录，待决定「默认按主键 id 排序」或「移除该重载」后调整本用例。
    assertThatThrownBy(() -> service.findByPage(0, 10, Sort.Direction.ASC))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("At least one property must be given");

    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("findByPage(specification, pageable) 透传条件与分页参数")
  void shouldFindBySpecificationAndPageable() {
    Specification<TestEntity> specification = specification();
    Pageable pageable = PageRequest.of(0, 10);
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    when(repository.findAll(specification, pageable)).thenReturn(page);

    assertThat(service.findByPage(specification, pageable)).isSameAs(page);

    verify(repository).findAll(specification, pageable);
  }

  @Test
  @DisplayName("findByPage(specification, pageNumber, pageSize) 组装分页参数")
  void shouldFindBySpecificationPageNumberAndSize() {
    Specification<TestEntity> specification = specification();
    Page<TestEntity> page = new PageImpl<>(List.of(new TestEntity()));
    when(repository.findAll(specification, PageRequest.of(3, 30))).thenReturn(page);

    assertThat(service.findByPage(specification, 3, 30)).isSameAs(page);

    verify(repository).findAll(specification, PageRequest.of(3, 30));
  }
}
