package tutorials4j.springboot3.data.amqp.delayqueue;

import lombok.Data;

/**
 * 订单
 *
 * @author Yun Jiao
 */
@Data
public class Order {
  private String orderId;
  private String data;
  private Integer payStatus;
  private Integer isCanceled;
  private Long createTime;
  private Long cancelTime;
}
