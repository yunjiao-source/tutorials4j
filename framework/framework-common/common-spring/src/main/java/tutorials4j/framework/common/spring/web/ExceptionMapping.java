package tutorials4j.framework.common.spring.web;

import tutorials4j.framework.common.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record ExceptionMapping(Class<? extends Exception> exceptionClass, ErrorCode errorCode) {}
