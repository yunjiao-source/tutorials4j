package tutorials4j.feature.basic.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.basic.entity.DictTypeEntity;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.feature.basic.model.DictTypeDTO;
import tutorials4j.feature.basic.model.DictTypeVO;
import tutorials4j.feature.basic.service.DictDataQuery;
import tutorials4j.feature.basic.service.DictDataService;
import tutorials4j.feature.basic.service.DictTypeQuery;
import tutorials4j.feature.basic.service.DictTypeService;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/basic/dict-type")
@RequiredArgsConstructor
public class DictTypeBasicController {
  private final DictTypeService dictTypeService;
  private final DictDataService dictDataService;

  @PostMapping("create")
  @ResponseStatus(HttpStatus.CREATED)
  public DictTypeVO create(@Valid @RequestBody DictTypeDTO dto) {
    DictTypeEntity entity = dictTypeService.create(dto);
    return DictTypeVO.of(entity);
  }

  @PutMapping("{id}")
  public DictTypeVO update(@PathVariable("id") String id, @Valid @RequestBody DictTypeDTO dto) {
    DictTypeEntity entity = dictTypeService.update(id, dto);
    return DictTypeVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    dictTypeService.delete(id);
  }

  @GetMapping("{id}")
  public DictTypeVO getById(@PathVariable("id") String id) {
    DictTypeEntity entity = dictTypeService.findById(id);
    return DictTypeVO.of(entity);
  }

  @GetMapping("page")
  public PagedModel<DictTypeVO> getByPage(DictTypeQuery query, Pageable pageable) {
    Page<DictTypeEntity> entities = dictTypeService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(DictTypeVO::of));
  }

  @GetMapping("root")
  public List<DictTypeVO> getRoot(
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    return dictTypeService.findRootByEnabled(allEnabled).stream().map(DictTypeVO::of).toList();
  }

  @GetMapping("{id}/children")
  public List<DictTypeVO> getChildren(
      @PathVariable("id") String id,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    return dictTypeService.findChildrenByEnabled(id, allEnabled).stream()
        .map(DictTypeVO::of)
        .toList();
  }

  @GetMapping("tree")
  public List<Tree<String>> getTree(
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    DictTypeQuery query = DictTypeQuery.builder().allEnabled(allEnabled).build();
    List<DictTypeEntity> entities = dictTypeService.findByQuery(query);

    return DictTypeVO.toTree(DictTypeVO.of(entities));
  }

  @GetMapping("/by-id/{id}/dict-data")
  public List<DictDataVO> getDictDataById(
      @PathVariable("id") String id,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    DictDataQuery query = DictDataQuery.builder().dictTypeId(id).allEnabled(allEnabled).build();
    return dictDataService.findByQuery(query).stream().map(DictDataVO::of).toList();
  }

  @GetMapping("/by-code/{code}/dict-data")
  public List<DictDataVO> getDictDataByCode(
      @PathVariable("code") String code,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    DictTypeEntity dictTypeEntity = dictTypeService.findByCode(code);
    return getDictDataById(dictTypeEntity.getId(), allEnabled);
  }

  @GetMapping("/by-id/{id}/dict-data/tree")
  public List<Tree<String>> getDictDataTreeById(
      @PathVariable("id") String id,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    List<DictDataVO> vos = getDictDataById(id, allEnabled);
    return DictDataVO.toTree(vos);
  }

  @GetMapping("/by-code/{code}/dict-data/tree")
  public List<Tree<String>> getDictDataTreeByCode(
      @PathVariable("code") String code,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled) {
    DictTypeEntity dictTypeEntity = dictTypeService.findByCode(code);
    return getDictDataTreeById(dictTypeEntity.getId(), allEnabled);
  }
}
