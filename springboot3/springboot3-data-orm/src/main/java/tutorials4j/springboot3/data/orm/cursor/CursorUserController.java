package tutorials4j.springboot3.data.orm.cursor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器实现
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class CursorUserController {

  private final CursorUserService userService;

  /** 分页查询用户列表 */
  @GetMapping
  public ResponseEntity<PaginationResponse<CursorUser>> getUsers(PaginationRequest request) {
    PaginationResponse<CursorUser> response = userService.getUsers(request);
    return ResponseEntity.ok(response);
  }
}
