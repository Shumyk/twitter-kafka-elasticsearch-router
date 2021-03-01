package rocks.shumyk.route.twitter.kafka.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
public class TwitterKafkaProducer {

	private final String topic;
	private final KafkaProducer<String, String> producer;

	public TwitterKafkaProducer(final String topic) {
		this.topic = topic;

		final Properties properties = createProducerProperties();
		this.producer = new KafkaProducer<>(properties);
	}

	private Properties createProducerProperties() {
		final Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return properties;
	}

	public void produce(final String message) {
		final ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
		producer.send(record);
		producer.flush();
	}
}
