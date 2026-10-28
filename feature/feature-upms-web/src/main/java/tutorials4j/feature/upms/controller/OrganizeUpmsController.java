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
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.OrganizeDTO;
import tutorials4j.feature.upms.model.OrganizeVO;
import tutorials4j.feature.upms.model.RoleVO;
import tutorials4j.feature.upms.service.OrganizeQuery;
import tutorials4j.feature.upms.service.OrganizeService;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/upms/organizes")
@RequiredArgsConstructor
public class OrganizeUpmsController {
  private final OrganizeService organizeService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrganizeVO create(@Valid @RequestBody OrganizeDTO dto) {
    OrganizeEntity entity = organizeService.create(dto);
    return OrganizeVO.of(entity);
  }

  @PutMapping("{id}")
  public OrganizeVO update(@PathVariable("id") String id, @Valid @RequestBody OrganizeDTO dto) {
    OrganizeEntity entity = organizeService.update(id, dto);
    return OrganizeVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    organizeService.delete(id);
  }

  @GetMapping("{id}")
  public OrganizeVO getById(@PathVariable("id") String id) {
    OrganizeEntity entity = organizeService.findById(id);
    return OrganizeVO.of(entity);
  }

  @GetMapping("page")
  public PagedModel<OrganizeVO> getByPage(OrganizeQuery query, Pageable pageable) {
    Page<OrganizeEntity> entities = organizeService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(OrganizeVO::of));
  }

  @GetMapping("{id}/roles")
  public List<RoleVO> getRolesById(@PathVariable("id") String id) {
    Set<RoleEntity> entities = organizeService.findRolesById(id);
    return RoleVO.of(entities);
  }

  @GetMapping("{id}/children")
  public List<OrganizeVO> getChildrenById(@PathVariable("id") String id) {
    List<OrganizeEntity> entities = organizeService.findChildrenById(id);
    return OrganizeVO.of(entities);
  }

  @GetMapping("root")
  public List<OrganizeVO> getRoot(OrganizeQuery query) {
    query.setAllRoot(YesNoEnum.yes);
    List<OrganizeEntity> entities = organizeService.findByQuery(query);
    return OrganizeVO.of(entities);
  }

  @GetMapping("list")
  public List<OrganizeVO> getList(OrganizeQuery query) {
    List<OrganizeEntity> entities = organizeService.findByQuery(query);

    return OrganizeVO.of(entities);
  }

  @GetMapping("tree")
  public List<Tree<String>> getTree(OrganizeQuery query) {
    List<OrganizeEntity> entities = organizeService.findByQuery(query);

    return OrganizeVO.toTree(OrganizeVO.of(entities));
  }
}
