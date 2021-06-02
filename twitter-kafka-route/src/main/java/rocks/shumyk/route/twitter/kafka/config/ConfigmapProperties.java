package rocks.shumyk.route.twitter.kafka.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConfigurationProperties
public class ConfigmapProperties {

	@Getter private final Map<String, String> application = new HashMap<>();
	@Getter private final Map<String, Object> kafka = new HashMap<>();

	public String twitterConsumerKey() {
		return System.getenv("TWITTER_CONSUMER_KEY");
	}
	public String twitterConsumerSecret() {
		return System.getenv("TWITTER_CONSUMER_SECRET");
	}
	public String twitterToken() {
		return System.getenv("TWITTER_TOKEN");
	}
	public String twitterTokenSecret() {
		return System.getenv("TWITTER_TOKEN_SECRET");
	}

	@PostConstruct
	private void postConstruct() {
		log.info("application properties: {}", application);
		log.info("kafka properties: {}", kafka);
	}
}
