package tutorials4j.microservice.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.microservice.demo.BookEntity;
import tutorials4j.microservice.demo.repository.BookRepository;
import tutorials4j.microservice.demo.service.BookService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {
  private final BookRepository bookRepository;
  private final BookService bookService;

  /** 查询全部 */
  @GetMapping
  public List<BookEntity> list() {
    return bookRepository.findAll();
  }

  /** 根据 id 查询 */
  @GetMapping("/{id}")
  public BookEntity getById(@PathVariable("id") String id) {
    return bookService.findById(id);
  }

  /** 按作者查询 */
  @GetMapping("/by-author")
  public List<BookEntity> byAuthor(@RequestParam("author") String author) {
    return bookRepository.findByAuthor(author);
  }

  /** 书名关键字搜索 */
  @GetMapping("/search")
  public List<BookEntity> search(@RequestParam("keyword") String keyword) {
    return bookRepository.findByTitleContaining(keyword);
  }

  /** 查询价格大于指定值的书 */
  @GetMapping("/expensive")
  public List<BookEntity> expensive(@RequestParam("price") BigDecimal price) {
    return bookRepository.findByPriceGreaterThan(price);
  }

  /** 新增 */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookEntity create(@RequestBody BookEntity book) {
    return bookRepository.save(book);
  }

  /** 更新 */
  @PutMapping("/{id}")
  public BookEntity update(@PathVariable("id") String id, @RequestBody BookEntity book) {
    return bookService.update(id, book);
  }

  /** 删除 */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") String id) {
    bookService.delete(id);
  }
}
