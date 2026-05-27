package tutorials4j.springboot3.data.orm.cursor;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 服务层实现
 *
 * @author Yun Jiao
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CursorUserService {

  private final CursorUserRepository userRepository;

  /**
   * 分页查询用户列表
   *
   * @param request 分页请求
   * @return 分页响应
   */
  public PaginationResponse<CursorUser> getUsers(PaginationRequest request) {
    List<CursorUser> cursorUsers;
    String nextCursor = null;
    boolean hasMore = false;

    // 解析游标
    CursorUtils.Cursor cursor = CursorUtils.parseCursor(request.getCursor());

    if (cursor == null) {
      // 查询第一页
      cursorUsers = userRepository.findFirstPage(request.getPageSize() + 1); // 多查询一条，用于判断是否有更多数据
    } else {
      // 查询下一页
      cursorUsers =
          userRepository.findByCursor(
              cursor.getId(),
              cursor.getTimestamp(),
              request.getPageSize() + 1); // 多查询一条，用于判断是否有更多数据
    }

    // 判断是否有更多数据
    if (cursorUsers.size() > request.getPageSize()) {
      hasMore = true;
      cursorUsers = cursorUsers.subList(0, request.getPageSize()); // 移除多余的一条数据
    }

    // 生成下一页游标
    if (!cursorUsers.isEmpty()) {
      CursorUser lastCursorUser = cursorUsers.get(cursorUsers.size() - 1);
      nextCursor =
          CursorUtils.generateCursor(lastCursorUser.getId(), lastCursorUser.getCreatedAt());
    }

    // 构建响应
    PaginationResponse<CursorUser> response = new PaginationResponse<>();
    response.setData(cursorUsers);
    response.setNextCursor(nextCursor);
    response.setHasMore(hasMore);
    response.setTotal(userRepository.countTotal()); // 可选，根据需要决定是否返回总数据量

    return response;
  }
}
