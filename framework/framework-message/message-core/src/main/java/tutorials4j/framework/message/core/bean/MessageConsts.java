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
  String MESSAGE_QUEUE_MAIN = ":main";
  String MESSAGE_QUEUE_PROCESS = ":process";
  String MESSAGE_QUEUE_DEAD_LETTER = ":dead_letter";
  String MESSAGE_QUEUE_DELAY = ":delay";

  static String getMessageQueueMain(String queueName) {
    return MESSAGE_QUEUE_PREFIX + queueName + MESSAGE_QUEUE_MAIN;
  }

  static String getMessageQueueProcess(String queueName) {
    return MESSAGE_QUEUE_PREFIX + queueName + MESSAGE_QUEUE_PROCESS;
  }

  static String getMessageQueueDeadLetter(String queueName) {
    return MESSAGE_QUEUE_PREFIX + queueName + MESSAGE_QUEUE_DEAD_LETTER;
  }

  static String getMessageQueueDelay(String queueName) {
    return MESSAGE_QUEUE_PREFIX + queueName + MESSAGE_QUEUE_DELAY;
  }
}
