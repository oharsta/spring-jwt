package jwt.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public interface JsonMapper {

  ObjectMapper mapper = ObjectMapperWrapper.init();

  class ObjectMapperWrapper {
    private static ObjectMapper init() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JodaModule());
      objectMapper.registerModule(new AfterburnerModule());
      return objectMapper;
    }
  }

}
