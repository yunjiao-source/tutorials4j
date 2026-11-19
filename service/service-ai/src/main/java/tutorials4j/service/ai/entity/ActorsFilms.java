package tutorials4j.service.ai.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@JsonPropertyOrder({"actor", "movies"})
public record ActorsFilms(String actor, List<String> movies) {}
