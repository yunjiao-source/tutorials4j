package tutorials4j.service.ai.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * deepseek-flash是支持多模态， 但是DeepSeekChatModel不支持，也就是不支持media餐厨。会丢失media参数，其结果返回：
 *
 * <p>，但是DeepSeekChatModel不支持
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("multimodality")
@RequiredArgsConstructor
public class MultimodalityAiController {
  private final ChatFacadeCache chatFacadeCache;

  // 结果返回：I don’t see a picture attached in your message. Please upload the image (or paste it
  // here), and I’ll describe what’s in it.
  @GetMapping("deepseek")
  public Map<String, String> deepseek() {

    String content =
        chatFacadeCache
            .getDefault()
            .client()
            .prompt()
            .user(
                u ->
                    u.text("Explain what do you see on this picture?")
                        .media(
                            MimeTypeUtils.IMAGE_PNG,
                            new ClassPathResource("/images/multimodal.test.png")))
            .call()
            .content();

    return Map.of("completion", content);
  }

  /**
   * openid的配置参数： openai: api-key: ${DS_API_KEY} base-url: https://api.deepseek.com chat: options:
   * model: deepseek-flash 所以还是调用的deepSeek大模型，跟openai没有任何关系
   *
   * @return
   */
  @GetMapping("openai")
  public Map<String, String> openai() {

    String content =
        chatFacadeCache
            .getOpenai()
            .client()
            .prompt()
            .user(
                u ->
                    u.text("解释下你在图片中看到了什么?")
                        .media(
                            MimeTypeUtils.IMAGE_PNG,
                            new ClassPathResource("/images/multimodal.test.png")))
            .call()
            .content();

    return Map.of("completion", content);
  }
}
