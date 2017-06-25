package demo;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class GreetingClientFeignExample {

  public static void main(String[] args) throws InterruptedException {

    ApplicationContext app = SpringApplication.run(GreetingClientFeignExample.class, args);

    GreetingClient gc = app.getBean(GreetingClient.class);

    while (true) {
      try {
        log.info("Got response: {}", gc.greet("Tom"));
      } catch (Exception ex) {
        log.error("Oh oh {}", ex.getMessage());
      }
      TimeUnit.MILLISECONDS.sleep(500);
    }

  }
}

@FeignClient(name = "greeting-service", configuration = GreetingServiceConfig.class, fallback = GreetingClientFallback.class)
interface GreetingClient {

  @RequestMapping(method = RequestMethod.GET, value = "/greet", consumes = "application/json")
  Map<String, Object> greet(@RequestParam(value = "name", defaultValue = "World") String name);
}

@Component
class GreetingClientFallback implements GreetingClient {

  @Override
  public Map<String, Object> greet(String name) {
    return Collections.singletonMap("greeting", "Fallback greeting: " + name);
  }
}

class GreetingServiceConfig {

  @Value("${bubu.x-auth:xxx}")
  String xauthHeader;

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor("user", "test");
  }

  @Bean
  public RequestInterceptor customAuthInterceptor() {
    return (requestTemplate) -> {
      requestTemplate.header("x-auth-token", xauthHeader);
    };
  }
}
