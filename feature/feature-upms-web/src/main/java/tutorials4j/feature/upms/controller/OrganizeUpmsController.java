package tutorials4j.feature.upms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import tutorials4j.feature.upms.model.OrganizeDTO;
import tutorials4j.feature.upms.model.OrganizeVO;
import tutorials4j.feature.upms.service.OrganizeService;

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

  @PostMapping("create")
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
}
