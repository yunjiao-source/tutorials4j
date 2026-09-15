package tutorials4j.microservice.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.microservice.demo.BookEntity;
import tutorials4j.microservice.demo.repository.BookRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class BookService implements BaseService<BookEntity, String> {
  private final BookRepository bookRepository;

  @Override
  public BaseRepository<BookEntity, String> getRepository() {
    return bookRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public BookEntity update(String id, BookEntity book) {
    BookEntity existing = findById(id);
    existing.setTitle(book.getTitle());
    existing.setAuthor(book.getAuthor());
    existing.setPrice(book.getPrice());
    existing.setPublishDate(book.getPublishDate());
    return bookRepository.save(existing); // 脏检查机制下 save 可省略
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    bookRepository.findById(id).ifPresent(bookRepository::delete);
  }
}
