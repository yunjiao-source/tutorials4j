package tutorials4j.feature.oauth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import tutorials4j.feature.oauth.entity.ClientEntity;
import tutorials4j.feature.oauth.model.ClientCreateModel;
import tutorials4j.feature.oauth.model.ClientUpdateModel;
import tutorials4j.feature.oauth.service.ClientQuery;
import tutorials4j.feature.oauth.service.ClientService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/feature-oauth")
@RequiredArgsConstructor
public class ClientRestController {
  private final ClientService clientService;

  @PostMapping("create")
  @ResponseStatus(HttpStatus.CREATED)
  public ClientEntity create(@Valid @RequestBody ClientCreateModel model) {
    return clientService.create(model);
  }

  @PutMapping("{id}")
  public ClientEntity update(
      @PathVariable("id") String id, @Valid @RequestBody ClientUpdateModel model) {
    return clientService.update(id, model);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    clientService.delete(id);
  }

  @GetMapping("{id}")
  public ClientEntity getById(@PathVariable("id") String id) {
    return clientService.findById(id);
  }

  @GetMapping("page")
  public PagedModel<ClientEntity> getByPage(ClientQuery query, Pageable pageable) {
    var entities = clientService.findByPage(query, pageable);
    return new PagedModel<>(entities);
  }
}
