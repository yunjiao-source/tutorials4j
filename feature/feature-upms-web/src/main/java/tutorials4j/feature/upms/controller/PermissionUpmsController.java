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
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.PermissionCreateDTO;
import tutorials4j.feature.upms.model.PermissionUpdateDTO;
import tutorials4j.feature.upms.model.PermissionVO;
import tutorials4j.feature.upms.model.RoleVO;
import tutorials4j.feature.upms.service.PermissionQuery;
import tutorials4j.feature.upms.service.PermissionService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/upms/permissions")
@RequiredArgsConstructor
public class PermissionUpmsController {
  private final PermissionService permissionService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PermissionVO create(@Valid @RequestBody PermissionCreateDTO dto) {
    PermissionEntity entity = permissionService.create(dto);
    return PermissionVO.of(entity);
  }

  @PutMapping("{id}")
  public PermissionVO update(
      @PathVariable("id") String id, @Valid @RequestBody PermissionUpdateDTO dto) {
    PermissionEntity entity = permissionService.update(id, dto);
    return PermissionVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    permissionService.delete(id);
  }

  @GetMapping("{id}")
  public PermissionVO getById(@PathVariable("id") String id) {
    PermissionEntity entity = permissionService.findById(id);
    return PermissionVO.of(entity);
  }

  @GetMapping("{id}/roles")
  public List<RoleVO> getRolesById(@PathVariable("id") String id) {
    Set<RoleEntity> entities = permissionService.findRolesById(id);
    return RoleVO.of(entities);
  }

  @GetMapping("page")
  public PagedModel<PermissionVO> getByPage(PermissionQuery query, Pageable pageable) {
    Page<PermissionEntity> entities = permissionService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(PermissionVO::of));
  }
}
