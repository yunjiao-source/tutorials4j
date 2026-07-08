package tutorials4j.framework.message.redis.stream;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequest;
import org.springframework.data.redis.stream.Subscription;
import tutorials4j.framework.common.core.ExecutorServiceHolder;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.properties.RedisMessageProperties;
import tutorials4j.framework.message.redis.properties.RedisMessageProperties.StreamOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class StreamMessageConsumerFactory implements SmartInitializingSingleton {
  public static final String MESSAGE_TYPE = "stream";

  private final RedisMessageProperties properties;
  private final RedisConnectionFactory factory;
  private StreamMessageListenerContainer<String, ObjectRecord<String, BaseRedisMessage>>
      listenerContainer;
  private ExecutorServiceHolder<ThreadPoolExecutor> executorServiceHolder;

  private ConcurrentMap<String, Subscription> subscriptionMap = new ConcurrentHashMap<>();

  @Override
  public void afterSingletonsInstantiated() {
    initContainer();
  }

  @PreDestroy
  public void destroy() {
    if (executorServiceHolder != null) {
      executorServiceHolder.shutdown();
    }

    if (listenerContainer != null) {
      listenerContainer.stop();
    }
  }

  public void initContainer() {
    StreamOptions options = properties.getStream();
    if (options.getQueues().isEmpty()) {
      log.warn("Redis Stream 消息队列未配置");
      return;
    }

    this.executorServiceHolder = ExecutorServiceHolder.buildThreadPool(options.getExecution());
    StreamMessageListenerContainer.StreamMessageListenerContainerOptions<
            String, ObjectRecord<String, BaseRedisMessage>>
        containerOptions =
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(options.getPollTimeout())
                .batchSize(options.getCountPerRead())
                .targetType(BaseRedisMessage.class)
                .executor(executorServiceHolder.instance())
                .build();

    listenerContainer = StreamMessageListenerContainer.create(factory, containerOptions);

    options
        .getQueues()
        .forEach(
            (k, v) -> {
              if (StringUtils.isAnyBlank(v.getConsumerGroup(), v.getListenerBeanName())) {
                throw new IllegalStateException(
                    "流消息配置问题，consumerGroup="
                        + v.getConsumerGroup()
                        + ", listenerBeanName="
                        + v.getListenerBeanName());
              }
              if (v.getConsumerCount() <= 0) {
                throw new IllegalStateException("流消息配置问题，consumerCount 必须大于0");
              }

              Predicate<Throwable> cancelOnError =
                  (throwable) -> {
                    if (log.isDebugEnabled()) {
                      log.debug(
                          "流消息消费异常，是否取消消费：{}, 异常信息：{} {}",
                          v.isCancelOnError(),
                          throwable.getClass().getSimpleName(),
                          throwable.getMessage());
                    }
                    return v.isCancelOnError();
                  };

              StreamMessageConsumer streamMessageConsumer =
                  SpringUtil.getBean(v.getListenerBeanName(), StreamMessageConsumer.class);
              streamMessageConsumer.setQueueName(k);
              streamMessageConsumer.setStreamQueueOptions(v);

              for (int i = 0; i < v.getConsumerCount(); i++) {
                String streamKey = MessageConsts.getMessageQueueMain(MESSAGE_TYPE + ":" + k);

                Consumer consumer = Consumer.from(v.getConsumerGroup(), "instance-" + i);
                StreamReadRequest<String> request =
                    StreamReadRequest.builder(
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                        .consumer(consumer)
                        .cancelOnError(cancelOnError)
                        .autoAcknowledge(v.isAutoAck())
                        .build();
                subscriptionMap.computeIfAbsent(
                    consumer.toString(),
                    (key) ->
                        listenerContainer.register(request, streamMessageConsumer::handleMessage));
              }
            });

    listenerContainer.start();
  }
}
