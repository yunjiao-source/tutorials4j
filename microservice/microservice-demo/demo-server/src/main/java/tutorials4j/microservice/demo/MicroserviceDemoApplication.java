package tutorials4j.microservice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class MicroserviceDemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(MicroserviceDemoApplication.class, args);
  }
}
