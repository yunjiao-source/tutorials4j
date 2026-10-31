package tutorials4j.feature.basic.controller;

import cn.hutool.core.lang.tree.Tree;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import tutorials4j.feature.basic.entity.DictTypeEntity;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.feature.basic.model.DictTypeDTO;
import tutorials4j.feature.basic.model.DictTypeVO;
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DictTypeVO create(@Valid @RequestBody DictTypeDTO dto) {
    DictTypeEntity entity = dictTypeService.create(dto);
    return DictTypeVO.of(entity);
  }

  @PutMapping("{dict-type-id}")
  public DictTypeVO update(
      @PathVariable("dict-type-id") String id, @Valid @RequestBody DictTypeDTO dto) {
    DictTypeEntity entity = dictTypeService.update(id, dto);
    return DictTypeVO.of(entity);
  }

  @DeleteMapping("{dict-type-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("dict-type-id") String id) {
    dictTypeService.delete(id);
  }

  @GetMapping("{dict-type-id}")
  public DictTypeVO getById(@PathVariable("dict-type-id") String id) {
    DictTypeEntity entity = dictTypeService.findById(id);
    return DictTypeVO.of(entity);
  }

  @GetMapping("page")
  public PagedModel<DictTypeVO> getByPage(DictTypeQuery query, Pageable pageable) {
    Page<DictTypeEntity> entities = dictTypeService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(DictTypeVO::of));
  }

  @GetMapping("root")
  public List<DictTypeVO> getRoot(DictTypeQuery query) {
    query.setAllRoot(YesNoEnum.yes);
    List<DictTypeEntity> entities = dictTypeService.findByQuery(query);
    return DictTypeVO.of(entities);
  }

  @GetMapping("{dict-type-id}/children")
  public List<DictTypeVO> getChildren(
      @PathVariable("dict-type-id") String id, DictTypeQuery query) {
    query.setParentId(id);
    query.setAllRoot(YesNoEnum.no);
    List<DictTypeEntity> entities = dictTypeService.findByQuery(query);
    return DictTypeVO.of(entities);
  }

  @GetMapping("tree")
  public List<Tree<String>> getTree(DictTypeQuery query) {
    List<DictTypeEntity> entities = dictTypeService.findByQuery(query);
    return DictTypeVO.toTree(DictTypeVO.of(entities));
  }

  @GetMapping("/by-id/{dict-type-id}/dict-data")
  public List<DictDataVO> getDictDataById(@PathVariable("dict-type-id") String id) {
    return dictDataService.findByDictTypeId(id);
  }

  @GetMapping("/by-code/{dict-type-code}/dict-data")
  public List<DictDataVO> getDictDataByCode(@PathVariable("dict-type-code") String code) {
    DictTypeEntity dictTypeEntity = dictTypeService.findByCode(code);

    return getDictDataById(dictTypeEntity.getId());
  }

  @GetMapping("/by-id/{dict-type-id}/dict-data/tree")
  public List<Tree<String>> getDictDataTreeById(@PathVariable("dict-type-id") String id) {
    List<DictDataVO> vos = getDictDataById(id);
    return DictDataVO.toTree(vos);
  }

  @GetMapping("/by-code/{dict-type-code}/dict-data/tree")
  public List<Tree<String>> getDictDataTreeByCode(@PathVariable("dict-type-code") String code) {
    List<DictDataVO> vos = getDictDataByCode(code);
    return DictDataVO.toTree(vos);
  }

  @GetMapping("/all-with-map")
  public Map<String, List<DictDataVO>> getAllWithMap() {
    List<DictTypeEntity> entities = dictTypeService.getDictTypeRepository().findAllAndFetch();

    Map<String, List<DictDataVO>> resultMap = new HashMap<>();
    for (DictTypeEntity entity : entities) {
      resultMap.put(entity.getCode(), DictDataVO.of(entity.getDictDataList()));
    }
    return resultMap;
  }

  @GetMapping("/version")
  public Integer getVersion() {
    return 0;
  }
}
