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
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.MenuVO;
import tutorials4j.feature.upms.model.OrganizeVO;
import tutorials4j.feature.upms.model.PermissionVO;
import tutorials4j.feature.upms.model.RoleCreateDTO;
import tutorials4j.feature.upms.model.RoleUpdateDTO;
import tutorials4j.feature.upms.model.RoleVO;
import tutorials4j.feature.upms.service.RoleQuery;
import tutorials4j.feature.upms.service.RoleService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RestController
@RequestMapping("/upms/roles")
@RequiredArgsConstructor
public class RoleUpmsController {
  private final RoleService roleService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RoleVO create(@Valid @RequestBody RoleCreateDTO dto) {
    RoleEntity entity = roleService.create(dto);
    return RoleVO.of(entity);
  }

  @PutMapping("{id}")
  public RoleVO update(@PathVariable("id") String id, @Valid @RequestBody RoleUpdateDTO dto) {
    RoleEntity entity = roleService.update(id, dto);
    return RoleVO.of(entity);
  }

  @PutMapping("{id}/permissions")
  public RoleVO addPermissions(@PathVariable("id") String id, Set<String> permissionCodes) {
    RoleEntity entity = roleService.addPermissions(id, permissionCodes);
    return RoleVO.of(entity);
  }

  @PutMapping("{id}/menus")
  public RoleVO addMenus(@PathVariable("id") String id, Set<String> menuIds) {
    RoleEntity entity = roleService.addMenus(id, menuIds);
    return RoleVO.of(entity);
  }

  @PutMapping("{id}/organizes")
  public RoleVO addOrganizes(@PathVariable("id") String id, Set<String> organizeIds) {
    RoleEntity entity = roleService.addOrganizes(id, organizeIds);
    return RoleVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    roleService.delete(id);
  }

  @GetMapping("{id}")
  public RoleVO getById(@PathVariable("id") String id) {
    RoleEntity entity = roleService.findById(id);
    return RoleVO.of(entity);
  }

  @GetMapping("{id}/permissions")
  public List<PermissionVO> getPermissionsById(@PathVariable("id") String id) {
    RoleEntity entity = roleService.findById(id);
    return PermissionVO.of(entity.getPermissions());
  }

  @GetMapping("{id}/menus")
  public List<MenuVO> getMenusById(@PathVariable("id") String id) {
    RoleEntity entity = roleService.findById(id);
    return MenuVO.of(entity.getMenus());
  }

  @GetMapping("{id}/organizes")
  public List<OrganizeVO> getOrganizesById(@PathVariable("id") String id) {
    RoleEntity entity = roleService.findById(id);
    return OrganizeVO.of(entity.getOrganizes());
  }

  @GetMapping("page")
  public PagedModel<RoleVO> getByPage(RoleQuery query, Pageable pageable) {
    Page<RoleEntity> entities = roleService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(RoleVO::of));
  }
}
