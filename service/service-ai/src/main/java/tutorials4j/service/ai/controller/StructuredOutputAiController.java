package tutorials4j.service.ai.controller;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.list;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.service.ai.converter.LenientJsonOutputConverter;
import tutorials4j.service.ai.entity.ActorsFilms;
import tutorials4j.service.ai.support.ChatFacade;
import tutorials4j.service.ai.support.ChatFacadeCache;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("structured-output")
@RequiredArgsConstructor
public class StructuredOutputAiController {
  private final ChatFacade chatFacade;
  private final ChatFacadeCache chatFacadeCache;

  @GetMapping("typed-response")
  public Map<String, ActorsFilms> typedResponse() {

    ActorsFilms films =
        chatFacade
            .client()
            .prompt()
            .user("Generate the filmography for a random actor.")
            .call()
            .entity(ActorsFilms.class);

    return Map.of("completion", films);
  }

  @GetMapping("generic-types")
  public Map<String, List<ActorsFilms>> genericTypes() {

    List<ActorsFilms> films =
        chatFacade
            .client()
            .prompt()
            .user("Generate filmographies for three random actors.")
            .call()
            .entity(new ParameterizedTypeReference<List<ActorsFilms>>() {});

    return Map.of("completion", films);
  }

  @GetMapping("getting-the-full-response")
  public Map<String, Object> gettingTheFullResponse() {

    ResponseEntity<ChatResponse, ActorsFilms> result =
        chatFacade
            .client()
            .prompt()
            .user("Generate the filmography for a random actor.")
            .call()
            .responseEntity(ActorsFilms.class);

    ActorsFilms films = result.entity();
    ChatResponse raw = result.response();
    long totalTokens = raw.getMetadata().getUsage().getTotalTokens();

    return Map.of("completion", films, "raw", raw, "totalTokens", totalTokens);
  }

  @GetMapping("customizing-the-advisor")
  public Map<String, ActorsFilms> customizingTheAdvisor() {

    ActorsFilms films =
        getCustomizAdvisorChatFacade()
            .client()
            .prompt()
            .user("Generate the filmography for a random actor.")
            .call()
            .entity(ActorsFilms.class); // 此处无需再调用 validateSchema()

    return Map.of("completion", films);
  }

  @GetMapping("typed-response-converter")
  public Map<String, ActorsFilms> typedResponseConverter() {

    BeanOutputConverter<ActorsFilms> beanOutputConverter =
        new BeanOutputConverter<>(ActorsFilms.class);

    String format = beanOutputConverter.getFormat();

    String actor = "Tom Hanks";

    String template =
        """
        Generate the filmography of 5 movies for {actor}.
        {format}
        """;

    Generation generation =
        chatFacade
            .model()
            .call(
                PromptTemplate.builder()
                    .template(template)
                    .variables(Map.of("actor", actor, "format", format))
                    .build()
                    .create())
            .getResult();

    ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getText());

    return Map.of("completion", actorsFilms);
  }

  @GetMapping("generic-types-converter")
  public Map<String, List<ActorsFilms>> genericTypesConverter() {
    BeanOutputConverter<List<ActorsFilms>> outputConverter =
        new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorsFilms>>() {});

    String format = outputConverter.getFormat();
    String template =
        """
        Generate the filmography of 5 movies for Tom Hanks and Bill Murray.
        {format}
        """;

    Prompt prompt =
        PromptTemplate.builder()
            .template(template)
            .variables(Map.of("format", format))
            .build()
            .create();

    Generation generation = chatFacade.model().call(prompt).getResult();

    List<ActorsFilms> actorsFilms = outputConverter.convert(generation.getOutput().getText());
    return Map.of("completion", actorsFilms);
  }

  @GetMapping("map-output")
  public Map<String, Map<String, Object>> mapOutput() {
    Map<String, Object> result =
        chatFacade
            .client()
            .prompt()
            .user(
                u ->
                    u.text("Provide me a List of {subject}")
                        .param(
                            "subject",
                            "an array of numbers from 1 to 9 under they key name 'numbers'"))
            .call()
            .entity(new ParameterizedTypeReference<Map<String, Object>>() {});

    return Map.of("completion", result);
  }

  @GetMapping("map-output-converter")
  public Map<String, Map<String, Object>> mapOutputConverter() {
    MapOutputConverter mapOutputConverter = new MapOutputConverter();

    String format = mapOutputConverter.getFormat();
    String template =
        """
        Provide me a List of {subject}
        {format}
        """;

    Prompt prompt =
        PromptTemplate.builder()
            .template(template)
            .variables(
                Map.of(
                    "subject",
                    "an array of numbers from 1 to 9 under they key name 'numbers'",
                    "format",
                    format))
            .build()
            .create();

    Generation generation = chatFacade.model().call(prompt).getResult();

    Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getText());

    return Map.of("completion", result);
  }

  @GetMapping("list-output")
  public Map<String, List<String>> listOutput() {
    List<String> flavors =
        chatFacade
            .client()
            .prompt()
            .user(u -> u.text("List five {subject}").param("subject", "ice cream flavors"))
            .call()
            .entity(new ListOutputConverter(new DefaultConversionService()));

    return Map.of("completion", flavors);
  }

  @GetMapping("list-output-converter")
  public Map<String, List<String>> listOutputConverter() {
    ListOutputConverter listOutputConverter =
        new ListOutputConverter(new DefaultConversionService());

    String format = listOutputConverter.getFormat();
    String template =
        """
        List five {subject}
        {format}
        """;

    Prompt prompt =
        PromptTemplate.builder()
            .template(template)
            .variables(Map.of("subject", "ice cream flavors", "format", format))
            .build()
            .create();

    Generation generation = chatFacade.model().call(prompt).getResult();

    List<String> list = listOutputConverter.convert(generation.getOutput().getText());
    return Map.of("completion", list);
  }

  @GetMapping("custom-converter")
  public Map<String, ActorsFilms> customConverter() {
    ActorsFilms films =
        chatFacade
            .client()
            .prompt()
            .user("Generate the filmography for a random actor.")
            .call()
            .entity(new LenientJsonOutputConverter<>(ActorsFilms.class));
    return Map.of("completion", films);
  }

  private ChatFacade getCustomizAdvisorChatFacade() {
    return chatFacadeCache.getDefault(
        "customizing-the-advisor",
        builder -> {
          // 自动生成对应的 JSON Schema
          StructuredOutputConverter<ActorsFilms> myConverter =
              new BeanOutputConverter<>(ActorsFilms.class);

          // 手动构建 Advisor
          var validationAdvisor =
              StructuredOutputValidationAdvisor.builder()
                  // .outputType(ActorsFilms.class) // 指定期望的输出类型，Schema会自动生成
                  .outputJsonSchema(
                      myConverter.getJsonSchema()) // 直接传入预置的Schema字符串, 与 outputType 的互斥关系
                  .maxRepeatAttempts(2) // 自定义最大重试次数
                  // .objectMapper(myCustomObjectMapper) // 传入自定义的 ObjectMapper
                  .build();

          builder.defaultAdvisors(validationAdvisor);
        });
  }
}
