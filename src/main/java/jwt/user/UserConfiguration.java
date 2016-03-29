package jwt.user;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongeez.MongeezRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.net.UnknownHostException;

@Configuration
public class UserConfiguration {

  @Value("${spring.data.mongodb.database}")
  private String databaseName;

  @Bean
  public MongeezRunner mongeezRunner() throws UnknownHostException {
    MongeezRunner mongeez = new MongeezRunner();
    mongeez.setExecuteEnabled(true);
    mongeez.setDbName(databaseName);
    mongeez.setFile(new ClassPathResource("db/mongeez.xml"));
    mongeez.setMongo(new MongoClient());
    return mongeez;
  }


}
