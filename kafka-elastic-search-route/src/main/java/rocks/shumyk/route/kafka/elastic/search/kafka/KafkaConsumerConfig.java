package rocks.shumyk.route.kafka.elastic.search.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rocks.shumyk.route.kafka.elastic.search.config.ConfigmapProperties;

import static java.util.Arrays.asList;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

	@Bean
	public KafkaConsumer<String, String> initiateKafkaConsumer(final ConfigmapProperties properties) {
		final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties.getKafkaProperties());

		final var topics = asList(properties.getApplication().get("kafka.topics").split(","));
		consumer.subscribe(topics);

		log.info("Created Kafka consumer and subscribed to topics: {}", topics);
		return consumer;
	}
}
