package jwt.security;

import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class NoopAuthenticationEventPublisher implements AuthenticationEventPublisher {

  @Override
  public void publishAuthenticationSuccess(Authentication authentication) {
  }

  @Override
  public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
  }
}
