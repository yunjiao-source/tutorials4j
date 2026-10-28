package tutorials4j.feature.basic.controller;

import jakarta.validation.Valid;
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
import tutorials4j.feature.basic.entity.DictDataEntity;
import tutorials4j.feature.basic.model.DictDataDTO;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.feature.basic.service.DictDataQuery;
import tutorials4j.feature.basic.service.DictDataService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/basic/dict-data")
@RequiredArgsConstructor
public class DictDataBasicController {
  private final DictDataService dictDataService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DictDataVO create(@Valid @RequestBody DictDataDTO dto) {
    DictDataEntity entity = dictDataService.create(dto);
    return DictDataVO.of(entity);
  }

  @PutMapping("{id}")
  public DictDataVO update(@PathVariable("id") String id, @Valid @RequestBody DictDataDTO dto) {
    DictDataEntity entity = dictDataService.update(id, dto);
    return DictDataVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    dictDataService.delete(id);
  }

  @GetMapping("{id}")
  public DictDataVO getById(@PathVariable("id") String id) {
    DictDataEntity entity = dictDataService.findById(id);
    return DictDataVO.of(entity);
  }

  @GetMapping("page")
  public PagedModel<DictDataVO> getByPage(DictDataQuery query, Pageable pageable) {
    Page<DictDataEntity> entities = dictDataService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(DictDataVO::withDictType));
  }
}
