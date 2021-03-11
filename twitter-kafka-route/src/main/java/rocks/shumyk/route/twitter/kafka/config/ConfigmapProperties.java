package rocks.shumyk.route.twitter.kafka.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties
public class ConfigmapProperties {

	public static final String ROUTE_PROPERTIES = "route";

	@Getter private final Map<String, String> route = new HashMap<>();
}
