package tutorials4j.framework.message.core.bean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface MessageConsts {
  String MESSAGE_KEY_EMAIL = "email";

  String MESSAGE_KEY_SMS = "sms";

  String MESSAGE_QUEUE_PREFIX = "message:";

  String MESSAGE_QUEUE_SUFFIX_PROCESS = ":process";
  String MESSAGE_QUEUE_SUFFIX_DEAD_LETTER = ":dead_letter";

  String MESSAGE_QUEUE_SUFFIX_DELAY = ":delay";

  String MESSAGE_QUEUE_SUFFIX_IDEMPOTENT = ":idempotent";

  static String getMessageQueue(String messageType, String queueName) {
    return MESSAGE_QUEUE_PREFIX + queueName + ":" + messageType;
  }
}
