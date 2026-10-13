package tutorials4j.microservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@SpringBootApplication
public class MicroserviceGatewayApplication {
  public static void main(String[] args) {
    Hooks.enableAutomaticContextPropagation();
    SpringApplication.run(MicroserviceGatewayApplication.class, args);
  }
}
