package tutorials4j.springboot3;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * ftp服务
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class FTPService {
  private final FTPConfig ftpConfig;

  public void uploadFile(String remotePath, MultipartFile file) throws IOException {
    FTPClient ftpClient = new FTPClient();
    try {
      ftpClient.connect(ftpConfig.getServer(), ftpConfig.getPort());
      ftpClient.login(ftpConfig.getUser(), ftpConfig.getPassword());
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

      try (InputStream inputStream = file.getInputStream()) {
        boolean done = ftpClient.storeFile(remotePath, inputStream);
        if (done) {
          System.out.println(
              "File " + file.getOriginalFilename() + " has been uploaded successfully.");
        } else {
          throw new IOException("Failed to upload file " + file.getOriginalFilename());
        }
      }
    } finally {
      if (ftpClient.isConnected()) {
        ftpClient.logout();
        ftpClient.disconnect();
      }
    }
  }
}
