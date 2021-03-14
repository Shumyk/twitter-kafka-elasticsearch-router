package rocks.shumyk.route.twitter.kafka.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import static java.lang.System.getenv;
import static java.util.Objects.nonNull;
import static rocks.shumyk.route.twitter.kafka.util.KafkaUtils.createProducerProperties;

@Slf4j
@Component
public class TwitterKafkaProducer {

	private final String topic;
	private final KafkaProducer<String, String> producer;

	public TwitterKafkaProducer() {
		this.topic = getenv("TOPIC_TWEETS_RAW");
		final String kafkaBrokerHost = getenv("KAFKA_BROKER_HOST");
		log.info("KAFKA TOPIC : {}", topic);
		log.info("KAFKA BROKER HOST: {}", kafkaBrokerHost);
		this.producer = new KafkaProducer<>(createProducerProperties(kafkaBrokerHost));
	}

	public void produce(final String message) {
		final ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
		producer.send(record, producingCallback());
		producer.flush();
	}

	private Callback producingCallback() {
		return (recordMetadata, exception) -> {
			if (nonNull(exception)) {
				log.error("Error occurred during producing message to kafka", exception);
			} else {
				log.info("Message successfully published to Kafka.\n Topic: {}.\n Partitions: {}.\n Offset: {}.\n Timestamp: {}.", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
			}
		};
	}

	public void close() {
		producer.close();
	}
}
