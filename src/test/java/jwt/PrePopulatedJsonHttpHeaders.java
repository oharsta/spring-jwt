package jwt;

import org.junit.Before;
import org.springframework.http.HttpHeaders;

public class PrePopulatedJsonHttpHeaders extends HttpHeaders {

  public PrePopulatedJsonHttpHeaders() {
    super();
    this.add(HttpHeaders.CONTENT_TYPE, "application/json");
  }
}
