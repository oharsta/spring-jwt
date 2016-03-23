package jwt.security;

import jwt.user.MockUserManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${secret.key}")
  private String secretKey;

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(new JwtAuthenticationProvider(secretKey(), new MockUserManager()));
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf().disable()
        .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/admin/**").hasRole("ADMIN")
        .antMatchers("/**").hasRole("USER");
  }

  private SecretKey secretKey() {
    return new SecretKeySpec(secretKey.getBytes(), "AES");
  }
}
