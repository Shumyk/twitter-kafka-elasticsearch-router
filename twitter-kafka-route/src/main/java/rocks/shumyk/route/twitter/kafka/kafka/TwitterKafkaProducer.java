package rocks.shumyk.route.twitter.kafka.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TwitterKafkaProducer {

	private final String topic;
	private final KafkaProducer<String, String> producer;

	public TwitterKafkaProducer(/*final String topic*/) {
		this.topic = "tweets_topic";// todo read topic from env

		final Properties properties = createProducerProperties();
		this.producer = new KafkaProducer<>(properties);

		// add shutdown hook // todo move to separate and add twitter client closing
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Stopping Application [kafka]");
			producer.close();
			log.info("Application is stopped.");
		}));
	}

	private Properties createProducerProperties() {
		final Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		// properties to ensure we have safe producer
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		// enable high throughput producer (at the expense of a bit of latency and CPU usage)
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)); // 32 KB
		return properties;
	}

	public void produce(final String message) {
		final ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
		producer.send(record, producingCallback());
		producer.flush();
	}

	private Callback producingCallback() {
		return (data, exception) -> {
			if (nonNull(exception)) {
				log.error("Error occurred during producing message to kafka", exception);
			} else {
				// todo add record metadata info to log
				log.info("Message successfully published to Kafka");
			}
		};
	}
}
