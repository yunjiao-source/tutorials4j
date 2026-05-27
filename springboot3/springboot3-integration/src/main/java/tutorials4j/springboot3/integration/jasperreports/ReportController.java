package tutorials4j.springboot3.integration.jasperreports;

import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("/users")
public class ReportController {
  private final Faker faker = new Faker();

  @GetMapping("/export/{format}")
  public ResponseEntity<Resource> export(@PathVariable("format") String format) throws Exception {
    // 模拟数据
    List<User> users = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      users.add(
          new User(
              faker.idNumber().ssnValid(),
              faker.name().fullName(),
              faker.number().numberBetween(10, 80),
              faker.internet().emailAddress(),
              faker.address().fullAddress()));
    }

    // 生成报表
    byte[] content = ReportGenerator.generate(users, format);
    ByteArrayResource resource = new ByteArrayResource(content);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("user-report." + format).build().toString())
        .contentLength(resource.contentLength())
        .body(resource);
  }
}
