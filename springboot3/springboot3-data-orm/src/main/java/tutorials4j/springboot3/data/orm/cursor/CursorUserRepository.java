package tutorials4j.springboot3.data.orm.cursor;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 仓库层实现
 *
 * @author Yun Jiao
 */
@Repository
public interface CursorUserRepository extends JpaRepository<CursorUser, Long> {

  /**
   * 基于游标查询用户列表
   *
   * @param id ID 游标
   * @param timestamp 时间戳游标
   * @param pageSize 每页大小
   * @return 用户列表
   */
  @Query(
      value =
          "SELECT * FROM t_cursor_user WHERE (created_at < :timestamp) OR (created_at = :timestamp AND id < :id) ORDER BY created_at DESC, id DESC LIMIT :pageSize",
      nativeQuery = true)
  List<CursorUser> findByCursor(
      @Param("id") Long id,
      @Param("timestamp") LocalDateTime timestamp,
      @Param("pageSize") Integer pageSize);

  /**
   * 查询第一页用户列表
   *
   * @param pageSize 每页大小
   * @return 用户列表
   */
  @Query(
      value = "SELECT * FROM t_cursor_user ORDER BY created_at DESC, id DESC LIMIT :pageSize",
      nativeQuery = true)
  List<CursorUser> findFirstPage(@Param("pageSize") Integer pageSize);

  /**
   * 查询总数据量
   *
   * @return 总数据量
   */
  @Query(value = "SELECT COUNT(*) FROM t_cursor_user", nativeQuery = true)
  long countTotal();
}
