package rocks.shumyk.route.kafka.elastic.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class KafkaElasticSearchRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaElasticSearchRouteApplication.class, args);
	}
}
