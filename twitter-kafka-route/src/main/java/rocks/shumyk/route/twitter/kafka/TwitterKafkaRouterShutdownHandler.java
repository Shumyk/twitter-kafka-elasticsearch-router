package rocks.shumyk.route.twitter.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.twitter.kafka.kafka.TwitterKafkaProducer;
import rocks.shumyk.route.twitter.kafka.twitter.TwitterDataProcessor;

@Slf4j
@Component
public class TwitterKafkaRouterShutdownHandler {

	public TwitterKafkaRouterShutdownHandler(final TwitterKafkaProducer kafkaProducer,
											 final TwitterDataProcessor dataProcessor) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Stopping Application [{}, {}]", kafkaProducer.getClass(), dataProcessor.getClass());
			dataProcessor.close();
			kafkaProducer.close();
			log.info("Application is stopped.");
		}));
	}
}
