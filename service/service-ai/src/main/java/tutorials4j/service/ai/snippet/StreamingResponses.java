package tutorials4j.service.ai.snippet;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class StreamingResponses {
  private final ChatClient chatClient;

  public Flux<String> joke() {
    return chatClient.prompt().user("Tell me a joke").stream().content();
  }

  public List<ActorFilms> entities() {
    var converter =
        new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorFilms>>() {});

    Flux<String> flux =
        this.chatClient
            .prompt()
            .user(
                u ->
                    u.text(
                            """
                        Generate the filmography for a random actor.
                        {format}
                      """)
                        .param("format", converter.getFormat()))
            .stream()
            .content();

    String content = flux.collectList().block().stream().collect(Collectors.joining());

    return converter.convert(content);
  }
}
