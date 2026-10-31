package tutorials4j.feature.upms.controller;

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
import tutorials4j.feature.upms.entity.ClientEntity;
import tutorials4j.feature.upms.model.ClientCreateDTO;
import tutorials4j.feature.upms.model.ClientUpdateDTO;
import tutorials4j.feature.upms.service.ClientQuery;
import tutorials4j.feature.upms.service.ClientService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RestController
@RequestMapping("/upms/clients")
@RequiredArgsConstructor
public class ClientUpmsController {
  private final ClientService clientService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientEntity create(@Valid @RequestBody ClientCreateDTO dto) {
    return clientService.create(dto);
  }

  @PutMapping("{menu-id}")
  public ClientEntity update(
      @PathVariable("menu-id") String id, @Valid @RequestBody ClientUpdateDTO dto) {
    return clientService.update(id, dto);
  }

  @DeleteMapping("{menu-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("menu-id") String id) {
    clientService.delete(id);
  }

  @GetMapping("{menu-id}")
  public ClientEntity getById(@PathVariable("menu-id") String id) {
    return clientService.findById(id);
  }

  @GetMapping("page")
  public PagedModel<ClientEntity> getByPage(ClientQuery query, Pageable pageable) {
    var entities = clientService.findByPage(query, pageable);
    return new PagedModel<>(entities);
  }
}
