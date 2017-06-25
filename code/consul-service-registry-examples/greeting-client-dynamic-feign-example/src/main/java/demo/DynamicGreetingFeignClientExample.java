package demo;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Query Prod Services HTTP Catalog API
 * <pre>{@code
 * curl http://localhost:8500/v1/catalog/service/greeting-service\?tag\=prod | jq .
 * }</pre>
 * <p>
 * Query Test Services HTTP Catalog API
 * <pre>{@code
 * curl http://localhost:8500/v1/catalog/service/greeting-service\?tag\=test | jq .
 * }</pre>
 * <p>
 * Query Test Services via DNS
 * <pre>{@code
 * dig @127.0.0.1 -p 8600 prod.greeting-service.service.consul SRV
 * }</pre>
 * <p>
 * Query Test Services via DNS
 * <pre>{@code
 * dig @127.0.0.1 -p 8600 test.greeting-service.service.consul SRV
 * }</pre>
 * <p>
 * dig @127.0.0.1 -p 8600 prod.greeting-service.service.dc1.consul SRV
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class DynamicGreetingFeignClientExample {

  private static final Logger log = LoggerFactory.getLogger(DynamicGreetingFeignClientExample.class);

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(DynamicGreetingFeignClientExample.class, args);
  }

  @Bean
  CommandLineRunner cli(GreetingClient gc) {
    return (args) -> {

      while (true) {
        try {
          log.info("Got response: {}", gc.greet("Tom"));
        } catch (Exception ex) {
          log.error("Oh oh {}", ex.getMessage());
        }
        TimeUnit.MILLISECONDS.sleep(500);
      }
    };
  }
}

@FeignClient(name = "http://greeting-service", configuration = GreetingServiceConfig.class, fallback = GreetingClientFallback.class)
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

  @Value("${spring.application.name}")
  String applicationName;

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

//  @Bean
//  public RequestInterceptor userAgentInterceptor() {
//    return (requestTemplate) -> {
//      requestTemplate.header("User-Agent", applicationName + "/1.0.0");
//    };
//  }
}
