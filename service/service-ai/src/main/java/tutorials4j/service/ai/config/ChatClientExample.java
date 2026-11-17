package tutorials4j.service.ai.config;

import java.util.Scanner;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
// @Configuration
public class ChatClientExample {
  @Bean
  CommandLineRunner cli(ChatClient chatClient) {

    return args -> {
      var scanner = new Scanner(System.in);

      // Use the selected chat client
      System.out.print("\nEnter your question: ");
      String input = scanner.nextLine();
      String response = chatClient.prompt(input).call().content();
      System.out.println("ASSISTANT: " + response);

      scanner.close();
    };
  }
}
