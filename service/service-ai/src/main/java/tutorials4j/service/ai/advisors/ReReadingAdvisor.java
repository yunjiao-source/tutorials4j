package tutorials4j.service.ai.advisors;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;

/**
 * 一个 Spring AI {@link BaseAdvisor} 实现，用于在向大语言模型（LLM）发送请求之前 应用 <strong>Re-Reading（Re2）</strong>
 * 提示技术。
 *
 * <p>Re2 技术通过将输入问题呈现两次来提升推理能力：一次是原始问题， 另一次是在显式指令 {@code "Read the question again:"} 之后再次呈现问题。
 * 这为仅解码器（decoder-only）模型提供了一种“伪双向”上下文， 使问题后部的信息能够影响对前部内容的理解。该技术即插即用， 可与
 * Chain-of-Thought（CoT）等其他提示策略结合使用。
 *
 * <p>该 advisor 会使用原始用户文本渲染配置的 Re2 模板， 并据此增强 {@link ChatClientRequest} 中的用户消息。默认模板如下：
 *
 * <pre>{@code
 * {re2_input_query}
 * Read the question again: {re2_input_query}
 * }</pre>
 *
 * <p>该 advisor 在 {@link #after(ChatClientResponse, AdvisorChain)} 中不会修改响应。 成本考量：Re2
 * 技术会使输入提示词的长度翻倍，从而导致 API 调用成本显著增加。对于成本敏感的场景，需要谨慎评估是否启用。
 *
 * @author Yun Jiao
 */
public class ReReadingAdvisor implements BaseAdvisor {

  /** 默认的 Re2 提示模板。该模板包含原始问题， 并随后附加显式指令要求再次阅读问题。 */
  private static final String DEFAULT_RE2_ADVISE_TEMPLATE =
      """
			{re2_input_query}
			Read the question again: {re2_input_query}
			""";

  /** 用于增强用户消息的 Re2 提示模板。 */
  private final String re2AdviseTemplate;

  /** 该 advisor 在 advisor 链中的顺序，默认值为 {@code 0}。 */
  private int order = 0;

  /** 使用默认 Re2 模板创建 {@code ReReadingAdvisor}。 */
  public ReReadingAdvisor() {
    this(DEFAULT_RE2_ADVISE_TEMPLATE);
  }

  /**
   * 使用自定义 Re2 模板创建 {@code ReReadingAdvisor}。
   *
   * <p>传入的模板必须包含 {@code {re2_input_query}} 占位符， 该占位符会被替换为原始用户消息文本。
   *
   * @param re2AdviseTemplate 用于增强用户消息的 Re2 提示模板
   */
  public ReReadingAdvisor(String re2AdviseTemplate) {
    this.re2AdviseTemplate = re2AdviseTemplate;
  }

  /**
   * 在请求发送给 LLM 之前，对请求应用 Re2 技术。
   *
   * <p>该方法使用原始用户消息作为 {@code re2_input_query} 变量来渲染配置的 Re2 模板， 然后使用渲染后的文本增强请求中的用户消息。 返回的 {@link
   * ChatClientRequest} 是原始请求的变更副本。
   *
   * @param chatClientRequest 原始聊天客户端请求
   * @param advisorChain advisor 链
   * @return 一个变更后的请求，其用户消息包含经 Re2 增强的文本
   */
  @Override
  public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
    String augmentedUserText =
        PromptTemplate.builder()
            .template(this.re2AdviseTemplate)
            .variables(
                Map.of("re2_input_query", chatClientRequest.prompt().getUserMessage().getText()))
            .build()
            .render();

    return chatClientRequest
        .mutate()
        .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText))
        .build();
  }

  /**
   * 原样返回响应。
   *
   * <p>Re2 技术只影响请求，不需要对响应进行后处理。
   *
   * @param chatClientResponse 聊天客户端响应
   * @param advisorChain advisor 链
   * @return 原始 {@code chatClientResponse}
   */
  @Override
  public ChatClientResponse after(
      ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
    return chatClientResponse;
  }

  /**
   * 返回该 advisor 在 advisor 链中的顺序。
   *
   * @return advisor 顺序
   */
  @Override
  public int getOrder() {
    return this.order;
  }

  /**
   * 设置该 advisor 在 advisor 链中的顺序，并返回当前实例以支持链式调用。
   *
   * @param order advisor 顺序
   * @return 当前 {@code ReReadingAdvisor} 实例
   */
  public ReReadingAdvisor withOrder(int order) {
    this.order = order;
    return this;
  }
}
