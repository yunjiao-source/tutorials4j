package tutorials4j.feature.upms.controller;

import cn.hutool.core.lang.tree.Tree;
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
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.MenuDTO;
import tutorials4j.feature.upms.model.MenuVO;
import tutorials4j.feature.upms.model.RoleVO;
import tutorials4j.feature.upms.service.MenuQuery;
import tutorials4j.feature.upms.service.MenuService;
import tutorials4j.feature.upms.service.MenuSpecification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/upms/mens")
@RequiredArgsConstructor
public class MenuUpmsController {
  private final MenuService menuService;

  @PostMapping("create")
  @ResponseStatus(HttpStatus.CREATED)
  public MenuVO create(@Valid @RequestBody MenuDTO dto) {
    MenuEntity entity = menuService.create(dto);
    return MenuVO.of(entity);
  }

  @PutMapping("{id}")
  public MenuVO update(@PathVariable("id") String id, @Valid @RequestBody MenuDTO dto) {
    MenuEntity entity = menuService.update(id, dto);
    return MenuVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    menuService.delete(id);
  }

  @GetMapping("{id}")
  public MenuVO getById(@PathVariable("id") String id) {
    MenuEntity entity = menuService.findById(id);
    return MenuVO.of(entity);
  }

  @GetMapping("{id}/roles")
  public List<RoleVO> getRolesById(@PathVariable("id") String id) {
    Set<RoleEntity> entities = menuService.findRolesById(id);
    return RoleVO.of(entities);
  }

  @GetMapping("{id}/children")
  public List<MenuVO> getChildrenById(@PathVariable("id") String id) {
    List<MenuEntity> entities = menuService.findChildrenById(id);
    return MenuVO.of(entities);
  }

  @GetMapping("root")
  public List<MenuVO> getRootById() {
    List<MenuEntity> entities = menuService.findAll(MenuSpecification.allRoot());
    return MenuVO.of(entities);
  }

  @GetMapping("tree")
  public List<Tree<String>> getTreeById() {
    List<MenuEntity> entities = menuService.findAll();

    return MenuVO.toTree(MenuVO.of(entities));
  }

  @GetMapping("page")
  public PagedModel<MenuVO> getByPage(MenuQuery query, Pageable pageable) {
    Page<MenuEntity> entities = menuService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(MenuVO::of));
  }
}
