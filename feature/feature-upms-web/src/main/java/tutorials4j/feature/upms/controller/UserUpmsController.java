package tutorials4j.feature.upms.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.feature.upms.model.RoleVO;
import tutorials4j.feature.upms.model.UserCreateDTO;
import tutorials4j.feature.upms.model.UserUpdateDTO;
import tutorials4j.feature.upms.model.UserVO;
import tutorials4j.feature.upms.service.UserQuery;
import tutorials4j.feature.upms.service.UserService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RestController
@RequestMapping("/upms/users")
@RequiredArgsConstructor
public class UserUpmsController {
  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserVO create(@Valid @RequestBody UserCreateDTO dto) {
    UserEntity entity = userService.create(dto);
    return UserVO.of(entity);
  }

  @PutMapping("{id}")
  public UserVO update(@PathVariable("id") String id, @Valid @RequestBody UserUpdateDTO dto) {
    UserEntity entity = userService.update(id, dto);
    return UserVO.of(entity);
  }

  @PutMapping("{id}/roles")
  public UserVO addRoles(@PathVariable("id") String id, Set<String> roles) {
    UserEntity entity = userService.addRoles(id, roles);
    return UserVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    userService.delete(id);
  }

  @GetMapping("{id}")
  public UserVO getById(@PathVariable("id") String id) {
    UserEntity entity = userService.findById(id);
    return UserVO.of(entity);
  }

  @GetMapping("by-username/{username}")
  public UserVO getByCode(@PathVariable("username") String username) {
    UserEntity entity = userService.findByUsername(username);
    return UserVO.of(entity);
  }

  @GetMapping("{id}/roles")
  public List<RoleVO> getRolesById(@PathVariable("id") String id) {
    UserEntity entity = userService.findById(id);
    return RoleVO.of(entity.getRoles());
  }

  @GetMapping("page")
  public PagedModel<UserVO> getByPage(UserQuery query, Pageable pageable) {
    Page<UserEntity> entities = userService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(UserVO::withOrganize));
  }
}
