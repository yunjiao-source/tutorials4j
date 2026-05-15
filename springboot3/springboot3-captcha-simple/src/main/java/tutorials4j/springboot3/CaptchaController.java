package tutorials4j.springboot3;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 显示验证码图片的接口
 *
 * @author Yun Jiao
 */
@Controller
public class CaptchaController {

  @GetMapping("/captcha")
  public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
    // 生成验证码文本并存入 session
    Pair<String, BufferedImage> code = CaptchaUtil.createVerificationImage();
    session.setAttribute("captcha", code.getKey());

    // 设置响应类型为图片
    response.setContentType("image/png");
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);

    // 将图片写入响应流
    ImageIO.write(code.getRight(), "png", response.getOutputStream());
  }
}
