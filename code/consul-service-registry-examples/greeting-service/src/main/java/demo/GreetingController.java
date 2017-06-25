package demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
class GreetingController {

  private static final UUID SERVICE_ID = UUID.randomUUID();

  private final Environment env;

  private final GreetingProperties greetingProperties;

  @GetMapping("/greet")
  Object greet(@RequestParam(defaultValue = "World") String name, @RequestHeader Map<String, String> headers) {

    log.info("Processing greeting request with headers: {}", headers);

    Map<String, Object> map = new LinkedHashMap<>();

    map.put("app", env.getProperty("spring.application.name"));
    map.put("greeting", greetingProperties.getSalutation() + " " + name);
    map.put("globalProperty", greetingProperties.getGlobalProperty());
    map.put("serviceId", SERVICE_ID);
    map.put("profiles", Arrays.toString(env.getActiveProfiles()));
    map.put("headers", headers.entrySet().stream()
      .filter(m -> Arrays.asList("host").contains(m.getKey()) || m.getKey().startsWith("x-"))
      .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())));

    return map;

  }
}
