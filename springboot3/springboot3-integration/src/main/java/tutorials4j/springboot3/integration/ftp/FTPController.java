package tutorials4j.springboot3.integration.ftp;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ftp文件接口
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FTPController {
  private final FTPService ftpService;

  @PostMapping("/upload-file")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file)
      throws IOException {
    String remotePath = "/uploads/" + file.getOriginalFilename();
    ftpService.uploadFile(remotePath, file);
    return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
  }
}
