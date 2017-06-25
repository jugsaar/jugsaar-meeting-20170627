package demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class GreetingClientRestTemplateExample {

  @Bean
  @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) throws InterruptedException {

    ApplicationContext app = SpringApplication.run(GreetingClientRestTemplateExample.class, args);

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

@Component
class GreetingClient {

  private final RestTemplate restTemplate;

  public GreetingClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  Map<String, Object> greet(String name) {
    return restTemplate.getForObject("http://greeting-service/greet?name" + name, Map.class);
  }
}
