package tutorials4j.service.ai.util;

import org.springframework.ai.chat.client.ChatClient;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface Utils {
  static String multiStepWorkflow(ChatClient chatClient, String rawInput) {
    // 步骤 1: 从杂乱文本中提取数值和指标
    String step1Prompt =
        """
                从以下文本中提取所有数值及其对应的指标名称，以“数值: 指标”的格式逐行列出。
                文本：%s
                """
            .formatted(rawInput);
    String extractedValues = chatClient.prompt(step1Prompt).call().content();

    // 步骤 2: 将数值统一标准化为百分比格式（如果适用）
    String step2Prompt =
        """
                将以下“数值: 指标”列表中的数值统一转换为百分比格式（如果原值没有百分号，则附加%%）。
                列表：
                %s
                """;
    String standardizedValues =
        chatClient.prompt(String.format(step2Prompt, extractedValues)).call().content();

    // 步骤 3: 按数值降序排序
    String step3Prompt =
        """
                将以下百分比列表按数值从大到小降序排列，保持“数值: 指标”的格式。
                列表：
                %s
                """
            .formatted(standardizedValues);
    String sortedValues = chatClient.prompt(step3Prompt).call().content();

    // 步骤 4: 格式化为 Markdown 表格
    String step4Prompt =
        """
                将以下排序后的列表转换为 Markdown 表格，表头为“指标 | 数值”。
                列表：
                %s
                """
            .formatted(sortedValues);
    return chatClient.prompt(step4Prompt).call().content();
  }
}
