package tutorials4j.springboot3;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 表单页面与提交校验
 *
 * @author Yun Jiao
 */
@Controller
public class FormController {

  @GetMapping("/form")
  public String showForm() {
    return "form"; // 对应 form.html
  }

  @PostMapping("/submit")
  public String handleSubmit(
      @RequestParam("captchaInput") String captchaInput,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    String sessionCaptcha = (String) session.getAttribute("captcha");

    // 校验验证码（忽略大小写）
    if (sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(captchaInput)) {
      // 成功：可以处理业务逻辑
      redirectAttributes.addFlashAttribute("message", "验证码正确，提交成功！");
      // 可选：验证码使用后立即清除，防止重复使用
      session.removeAttribute("captcha");
    } else {
      redirectAttributes.addFlashAttribute("error", "验证码错误，请重试。");
    }
    return "redirect:/form";
  }
}
