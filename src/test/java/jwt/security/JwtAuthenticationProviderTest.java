package jwt.security;

import jwt.user.UserManager;
import org.junit.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class JwtAuthenticationProviderTest {

  private JwtAuthenticationProvider subject = new JwtAuthenticationProvider("secret", mock(UserManager.class));

  @Test
  public void testSupports() throws Exception {
    assertTrue(subject.supports(JwtAuthenticationToken.class));
    assertTrue(subject.supports(UsernamePasswordAuthenticationToken.class));

    assertFalse(subject.supports(AbstractAuthenticationToken.class));
    assertFalse(subject.supports(Object.class));

  }
}