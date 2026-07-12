package tutorials4j.framework.common.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/** Unit tests for {@link ErrorCodeException}. */
@ExtendWith(MockitoExtension.class)
class ErrorCodeExceptionTest {

  @Mock private ErrorCode errorCode;

  @Mock private Feedback feedback;

  private static final String TEST_CODE = "TEST_001";
  private static final int TEST_HTTP_STATUS = 400;
  private static final String TEST_DETAIL = "Detail error message";
  private static final String TEST_PARAM_KEY = "userId";
  private static final Long TEST_PARAM_VALUE = 12345L;

  @BeforeEach
  void setUp() {
    lenient().when(errorCode.getFeedback()).thenReturn(feedback);
    lenient().when(feedback.getCode()).thenReturn(TEST_CODE);
    lenient().when(feedback.getHttpStatus()).thenReturn(TEST_HTTP_STATUS);
  }

  // ---------- Constructor tests ----------

  @Test
  void shouldCreateWithOnlyErrorCode() {
    ErrorCodeException ex = new ErrorCodeException(errorCode);
    assertSame(errorCode, ex.getErrorCode());
    assertNull(ex.getDetail());
    assertTrue(ex.getParams().isEmpty());
    assertNull(ex.getCause());
  }

  @Test
  void shouldCreateWithErrorCodeAndDetail() {
    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL);
    assertSame(errorCode, ex.getErrorCode());
    assertEquals(TEST_DETAIL, ex.getDetail());
    assertTrue(ex.getParams().isEmpty());
    assertNull(ex.getCause());
  }

  @Test
  void shouldCreateWithErrorCodeAndCause() {
    Throwable cause = new RuntimeException("Root cause");
    ErrorCodeException ex = new ErrorCodeException(errorCode, cause);
    assertSame(errorCode, ex.getErrorCode());
    assertNull(ex.getDetail());
    assertTrue(ex.getParams().isEmpty());
    assertSame(cause, ex.getCause());
  }

  @Test
  void shouldCreateWithErrorCodeDetailAndCause() {
    Throwable cause = new RuntimeException("Root cause");
    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL, cause);
    assertSame(errorCode, ex.getErrorCode());
    assertEquals(TEST_DETAIL, ex.getDetail());
    assertTrue(ex.getParams().isEmpty());
    assertSame(cause, ex.getCause());
  }

  @Test
  void shouldCreateWithErrorCodeDetailCauseAndParamMap() {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(TEST_PARAM_KEY, TEST_PARAM_VALUE);
    paramMap.put("anotherKey", "anotherValue");

    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL, null, paramMap);
    assertSame(errorCode, ex.getErrorCode());
    assertEquals(TEST_DETAIL, ex.getDetail());
    assertNull(ex.getCause());

    List<Pair<String, Object>> params = ex.getParams();
    assertEquals(2, params.size());
    assertTrue(
        params.stream()
            .anyMatch(
                p -> TEST_PARAM_KEY.equals(p.getKey()) && TEST_PARAM_VALUE.equals(p.getValue())));
    assertTrue(
        params.stream()
            .anyMatch(p -> "anotherKey".equals(p.getKey()) && "anotherValue".equals(p.getValue())));
  }

  @Test
  void shouldCreateWithErrorCodeDetailCauseAndNullParamMap() {
    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL, null, null);
    assertTrue(ex.getParams().isEmpty());
  }

  // ---------- param() chain method ----------

  @Test
  void shouldAddParamAndReturnThis() {
    ErrorCodeException ex = new ErrorCodeException(errorCode);
    ErrorCodeException returned = ex.param(TEST_PARAM_KEY, TEST_PARAM_VALUE);

    assertSame(ex, returned);
    List<Pair<String, Object>> params = ex.getParams();
    assertEquals(1, params.size());
    Pair<String, Object> pair = params.get(0);
    assertEquals(TEST_PARAM_KEY, pair.getKey());
    assertEquals(TEST_PARAM_VALUE, pair.getValue());
  }

  @Test
  void shouldSupportChainedParamCalls() {
    ErrorCodeException ex =
        new ErrorCodeException(errorCode).param("key1", "value1").param("key2", 2);

    List<Pair<String, Object>> params = ex.getParams();
    assertEquals(2, params.size());
    assertEquals("key1", params.get(0).getKey());
    assertEquals("value1", params.get(0).getValue());
    assertEquals("key2", params.get(1).getKey());
    assertEquals(2, params.get(1).getValue());
  }

  // ---------- getMessage() test ----------

  @Test
  void getMessageShouldIncludeFeedbackAndParams() {
    ErrorCodeException ex =
        new ErrorCodeException(errorCode, TEST_DETAIL).param(TEST_PARAM_KEY, TEST_PARAM_VALUE);

    String message = ex.getMessage();

    // Should contain the base message
    assertTrue(message.contains(TEST_DETAIL));

    // Should contain the exception context header
    assertTrue(message.contains("Exception Context:"));

    // Feedback entries appear first (CODE and HTTP_STATUS)
    assertTrue(message.contains("[1:CODE=" + TEST_CODE + "]"));
    assertTrue(message.contains("[2:HTTP_STATUS=" + TEST_HTTP_STATUS + "]"));

    // Custom param appears next (index 3)
    assertTrue(message.contains("[3:" + TEST_PARAM_KEY + "=" + TEST_PARAM_VALUE + "]"));

    // Should have the separator
    assertTrue(message.contains("---------------------------------"));
  }

  @Test
  void getMessageShouldHandleNullBaseMessage() {
    ErrorCodeException ex = new ErrorCodeException(errorCode);
    String message = ex.getMessage();
    // Should still contain context even if detail is null
    assertTrue(message.contains("Exception Context:"));
    assertTrue(message.contains("[1:CODE=" + TEST_CODE + "]"));
    assertTrue(message.contains("[2:HTTP_STATUS=" + TEST_HTTP_STATUS + "]"));
  }

  @Test
  void getMessageShouldHandleParamValueToStringException() {
    // Create an object that throws on toString()
    Object badObject =
        new Object() {
          @Override
          public String toString() {
            throw new RuntimeException("toString failed");
          }
        };

    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL).param("bad", badObject);

    String message = ex.getMessage();
    assertTrue(message.contains("Exception thrown on toString():"));
    assertTrue(message.contains("toString failed"));
  }

  // ---------- getResult() test ----------

  @Test
  void getResultShouldBuildResultWithCorrectValues() {
    // We need to mock the static Result.failure method.
    // Use Mockito's mockStatic (requires mockito-inline or Mockito 4.0+)
    try (var mockedStatic = mockStatic(Result.class)) {
      // Create a mock Result object to verify interactions
      Result<Void> mockResult = mock(Result.class);
      when(mockResult.errorDetail(anyString())).thenReturn(mockResult);
      when(mockResult.errorParams(anyList())).thenReturn(mockResult);

      // Stub the static factory
      mockedStatic.when(() -> Result.failure(errorCode)).thenReturn(mockResult);

      // Create exception with detail and params
      ErrorCodeException ex =
          new ErrorCodeException(errorCode, TEST_DETAIL).param(TEST_PARAM_KEY, TEST_PARAM_VALUE);

      Result<Void> result = ex.getResult();

      // Verify static method called with correct errorCode
      mockedStatic.verify(() -> Result.failure(errorCode), times(1));

      // Verify fluent setters called with correct values
      verify(mockResult, times(1)).errorDetail(TEST_DETAIL);
      verify(mockResult, times(1)).errorParams(ex.getParams());

      // The returned result should be the mocked one
      assertSame(mockResult, result);
    }
  }

  @Test
  void getResultShouldWorkWithNullDetailAndEmptyParams() {
    try (var mockedStatic = mockStatic(Result.class)) {
      Result<Void> mockResult = mock(Result.class);
      when(mockResult.errorDetail(any())).thenReturn(mockResult);
      when(mockResult.errorParams(any())).thenReturn(mockResult);

      mockedStatic.when(() -> Result.failure(errorCode)).thenReturn(mockResult);

      ErrorCodeException ex = new ErrorCodeException(errorCode);
      Result<Void> result = ex.getResult();

      mockedStatic.verify(() -> Result.failure(errorCode), times(1));
      verify(mockResult, times(1)).errorDetail(null);
      verify(mockResult, times(1)).errorParams(ex.getParams());
      assertSame(mockResult, result);
    }
  }

  // ---------- Additional edge cases ----------

  @Test
  void getMessageShouldIncludeAllParamsInOrder() {
    ErrorCodeException ex =
        new ErrorCodeException(errorCode).param("first", "A").param("second", "B");

    String message = ex.getMessage();
    // Check ordering: CODE (1), HTTP_STATUS (2), first (3), second (4)
    assertTrue(
        message.indexOf("[1:CODE=" + TEST_CODE + "]")
            < message.indexOf("[2:HTTP_STATUS=" + TEST_HTTP_STATUS + "]"));
    assertTrue(
        message.indexOf("[2:HTTP_STATUS=" + TEST_HTTP_STATUS + "]")
            < message.indexOf("[3:first=A]"));
    assertTrue(message.indexOf("[3:first=A]") < message.indexOf("[4:second=B]"));
  }

  @Test
  void constructorWithParamMapShouldAddParamsCorrectly() {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("key1", 100);
    paramMap.put("key2", null);

    ErrorCodeException ex = new ErrorCodeException(errorCode, TEST_DETAIL, null, paramMap);
    List<Pair<String, Object>> params = ex.getParams();
    assertEquals(2, params.size());
    assertTrue(params.contains(new ImmutablePair<>("key1", 100)));
    assertTrue(params.contains(new ImmutablePair<>("key2", null)));
  }
}
