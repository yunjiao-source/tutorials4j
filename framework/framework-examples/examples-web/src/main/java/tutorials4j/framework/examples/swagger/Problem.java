package tutorials4j.framework.examples.swagger;

/**
 * 问题响应记录，用于承载统一的错误信息（日志引用号与错误消息）。
 *
 * @param logRef 日志引用号，用于关联服务端日志
 * @param message 错误消息
 * @author Yun Jiao
 */
public record Problem(String logRef, String message) {}
