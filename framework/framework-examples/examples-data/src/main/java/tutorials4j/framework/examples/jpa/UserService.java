package tutorials4j.framework.examples.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

@Service
@RequiredArgsConstructor
public class UserService implements BaseService<User, Long> {

  private final UserRepository userRepository;

  @Override
  public BaseRepository<User, Long> getRepository() {
    return userRepository;
  }
}
