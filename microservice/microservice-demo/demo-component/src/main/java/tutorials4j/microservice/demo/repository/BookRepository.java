package tutorials4j.microservice.demo.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorials4j.microservice.demo.BookEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * @author Yun Jiao
 */
@Repository
public interface BookRepository extends BaseRepository<BookEntity, String> {
  /** 方法名派生查询：等价于 where author = ? */
  List<BookEntity> findByAuthor(String author);

  /** 模糊查询：where title like %keyword% */
  List<BookEntity> findByTitleContaining(String keyword);

  /** 自定义 JPQL 查询 */
  @Query("select b from BookEntity b where b.price > :price order by b.price asc")
  List<BookEntity> findByPriceGreaterThan(@Param("price") BigDecimal price);
}
