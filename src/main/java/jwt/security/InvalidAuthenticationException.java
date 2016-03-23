package jwt.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidAuthenticationException extends AuthenticationException {

  public InvalidAuthenticationException(String msg) {
    super(msg);
  }

  public InvalidAuthenticationException(String msg, Throwable t) {
    super(msg, t);
  }
}
