package jwt.web;

import jwt.security.InvalidAuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ErrorControllerTest {

  private ErrorController subject;
  private ErrorAttributes errorAttributes;

  @Before
  public void before() {
    errorAttributes = mock(ErrorAttributes.class);
    subject = new ErrorController(errorAttributes);
  }

  @Test
  public void testNoErrorDefaultResponseCode() {
    doTest(emptyMap(), null, 500);
  }

  @Test
  public void testNoErrorStatusResponseCode() {
    doTest(Collections.singletonMap("status", 301), null, 301);
  }

  @Test
  public void testNoAnnotatedError() {
    doTest(new HashMap<>(), new RuntimeException("test"), 500);
  }

  @Test
  public void testAnnotatedError() {
    doTest(new HashMap<>(), new InvalidAuthenticationException("test"), 403);
  }

  private void doTest(Map<String, Object> errorAttributes, Throwable throwable, int expectedStatusCode) {
    when(this.errorAttributes.getErrorAttributes(any(RequestAttributes.class), anyBoolean())).thenReturn(errorAttributes);
    when(this.errorAttributes.getError(any(RequestAttributes.class))).thenReturn(throwable);

    ResponseEntity<Map<String, Object>> response = subject.error(new MockHttpServletRequest());
    assertEquals(expectedStatusCode, response.getStatusCode().value());
  }

}