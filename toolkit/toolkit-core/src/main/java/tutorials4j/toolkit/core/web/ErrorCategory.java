package tutorials4j.toolkit.core.web;

import org.springframework.http.HttpStatus;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum ErrorCategory {
  SERVER("系统端异常", true),
  CLIENT("客户端异常", false),
  OTHER("其他异常", false);

  final String title;
  final boolean serverSide;

  ErrorCategory(String title, boolean serverSide) {
    this.title = title;
    this.serverSide = serverSide;
  }

  static ErrorCategory of(HttpStatus.Series series) {
    return switch (series) {
      case SERVER_ERROR -> SERVER;
      case CLIENT_ERROR -> CLIENT;
      default -> OTHER;
    };
  }
}
