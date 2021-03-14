package rocks.shumyk.route.twitter.kafka;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rocks.shumyk.route.twitter.kafka.kafka.TwitterKafkaProducer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SetEnvironmentVariable(key = "TOPIC_TWEETS_RAW", value = "tweets_raw")
@SetEnvironmentVariable(key = "KAFKA_BROKER_HOST", value = "kafka:9092")
class TwitterKafkaRouterApplicationTest {

	@MockBean private TwitterKafkaProducer producer;

	@Test void startupApplication() {
		assertTrue(true);
	}
}
