package tutorials4j.springboot3.data.amqp.orderedmessage;

import static tutorials4j.springboot3.data.amqp.orderedmessage.Consts.MAX_ORDER;

import com.github.javafaker.Faker;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DemoRunner {
  private final Faker faker = new Faker();
  private final OrderMessageSender orderMessageSender;
  private final OrderRepository orderRepository;
  private final Set<Long> createdSet = new HashSet<>();
  private final Set<Long> paySet = new HashSet<>();

  private final Set<Long> deliverSet = new HashSet<>();

  @Scheduled(fixedDelay = 5000)
  public void demoData() {
    IntStream.range(0, 10)
        .forEach(
            i -> {
              try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(499));
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }

              String messageType = "";
              Long orderId = faker.random().nextLong(MAX_ORDER);
              ;
              if (!createdSet.contains(orderId)) {
                messageType = "CREATE";
                createdSet.add(orderId);
              } else if (!paySet.contains(orderId)) {
                messageType = "PAY";
                paySet.add(orderId);
              } else if (!deliverSet.contains(orderId)) {
                messageType = "DELIVER";
                deliverSet.add(orderId);
              }

              if (StringUtils.isNotBlank(messageType)) {
                OrderMessage message = OrderMessage.of(orderId, messageType, "content");
                orderMessageSender.sendOrderMessage(message);
              }

              orderRepository.countOrder();
            });
  }
}
