package rocks.shumyk.route.kafka.elastic.search.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@ConfigurationProperties
public class ConfigmapProperties {

	@Getter	private final Map<String, String> application = new HashMap<>();
	@Getter private final Map<String, String> kafka = new HashMap<>();
	@Getter private final Properties kafkaProperties = new Properties();

	public static String elasticHostname() {
		return System.getenv("ELASTIC_HOSTNAME");
	}
	public static String elasticUsername() {
		return System.getenv("ELASTIC_USERNAME");
	}
	public static String elasticPassword() {
		return System.getenv("ELASTIC_PASSWORD");
	}

	@PostConstruct
	private void postConstruct() {
		kafka.forEach(kafkaProperties::put);
		log.info("application properties: {}", application);
		log.info("kafka string properties: {}", kafka);
		log.info("kafka properties: {}", kafkaProperties);
	}
}
