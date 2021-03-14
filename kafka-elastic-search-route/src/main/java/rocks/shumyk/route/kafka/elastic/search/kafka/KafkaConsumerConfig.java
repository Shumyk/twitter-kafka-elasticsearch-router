package rocks.shumyk.route.kafka.elastic.search.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

import static java.lang.System.getenv;
import static java.util.Arrays.asList;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

	@Bean
	public KafkaConsumer<String, String> initiateKafkaConsumer() {
		final String kafkaHost = getenv("KAFKA_HOST");
		final List<String> topics = asList(getenv("TOPIC_TWEETS_RAW").split(","));

		log.info("Kafka host: {}, topics to read: [{}]", kafkaHost, topics);

		final Properties consumerProperties = createConsumerProperties(kafkaHost);
		final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
		consumer.subscribe(topics);
		log.info("Create Kafka consumer and subscribed to topics");
		return consumer;
	}

	public static Properties createConsumerProperties(final String kafkaHost) {
		final Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit of offsets
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-tweets-consumer-group");
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
		return properties;
	}
}
