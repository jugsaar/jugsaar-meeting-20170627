package demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
class JokeController {

  private static final UUID SERVICE_ID = UUID.randomUUID();

  private static final String[] JOKES = {

    "\"Knock, knock.\"\n\n" +
      "\"Who’s there?\"\n\n" +
      "\"very long pause…\"\n\n" +
      " \n\n" +
      "Java."
    ,
    "\t\n" +
      "If you put a million monkeys at a million keyboards, one of them will eventually write a Java program.\n\nThe rest of them will write Perl programs."
    , "Two threads walk into a bar. The barkeeper looks up and yells, \"hey, I want don't any conditions race like time last!\""
  };

  private final Environment env;

  @GetMapping("/joke")
  Object greet(@RequestHeader Map<String, String> headers) {

    log.info("Processing joke request with headers: {}", headers);

    Map<String, Object> map = new LinkedHashMap<>();

    map.put("app", env.getProperty("spring.application.name"));
    map.put("joke", JOKES[(int) (Math.random() * JOKES.length)]);
    map.put("serviceId", SERVICE_ID);
    map.put("profiles", Arrays.toString(env.getActiveProfiles()));
    map.put("headers", headers.entrySet().stream()
      .filter(m -> Arrays.asList("host").contains(m.getKey()) || m.getKey().startsWith("x-"))
      .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())));

    return map;

  }
}
