package demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "greeting")
class GreetingProperties {

  // Consul Path: config/application/greeting/globalProperty
  String globalProperty;

  // Consul Path: config/application/greeting/salutation
  String salutation;
}
