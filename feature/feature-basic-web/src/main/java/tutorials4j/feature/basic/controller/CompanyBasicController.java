package tutorials4j.feature.basic.controller;

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
import tutorials4j.feature.basic.entity.CompanyEntity;
import tutorials4j.feature.basic.model.CompanyDTO;
import tutorials4j.feature.basic.model.CompanyVO;
import tutorials4j.feature.basic.service.CompanyService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/basic/companies")
@RequiredArgsConstructor
public class CompanyBasicController {
  private final CompanyService companyService;

  @PostMapping("create")
  @ResponseStatus(HttpStatus.CREATED)
  public CompanyVO create(@Valid @RequestBody CompanyDTO dto) {
    CompanyEntity entity = companyService.create(dto);
    return CompanyVO.of(entity);
  }

  @PutMapping("{id}")
  public CompanyVO update(@PathVariable("id") String id, @Valid @RequestBody CompanyDTO dto) {
    CompanyEntity entity = companyService.update(id, dto);
    return CompanyVO.of(entity);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    companyService.delete(id);
  }

  @GetMapping("{id}")
  public CompanyVO getById(@PathVariable("id") String id) {
    CompanyEntity entity = companyService.findById(id);
    return CompanyVO.of(entity);
  }

  @GetMapping("/by-organize-id/{organizeId}")
  public CompanyVO getByOrganizeId(@PathVariable("organizeId") String organizeId) {
    return companyService
        .getCompanyRepository()
        .findByOrganizeId(organizeId)
        .map(CompanyVO::of)
        .orElse(null);
  }
}
