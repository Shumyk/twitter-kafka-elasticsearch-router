package rocks.shumyk.route.twitter.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class TwitterKafkaRouterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterKafkaRouterApplication.class, args);
	}

}
