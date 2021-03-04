package rocks.shumyk.route.kafka.elastic.search;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static java.util.Collections.singleton;

public class TweetsKafkaConsumer {

	public static KafkaConsumer<String, String> initiateKafkaConsumer() {
		final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(createConsumerProperties());
		final String topic = "tweets_topic";
		consumer.subscribe(singleton(topic));
		return consumer;
	}

	private static Properties createConsumerProperties() {
		final Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-tweets-consumer-group");
		return properties;
	}
}
