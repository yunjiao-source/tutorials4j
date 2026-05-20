package tutorials4j.framework.examples.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章发布
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("article")
@RequiredArgsConstructor
public class ArticleController {

  @GetMapping
  public String publish() {
    return "发布成功！";
  }
}
