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
@SetEnvironmentVariable(key = "TWITTER_CONSUMER_KEY", value = "tcs")
@SetEnvironmentVariable(key = "TWITTER_CONSUMER_SECRET", value = "tcs")
@SetEnvironmentVariable(key = "TWITTER_TOKEN", value = "tt")
@SetEnvironmentVariable(key = "TWITTER_TOKEN_SECRET", value = "tts")
class TwitterKafkaRouterApplicationTest {

	@MockBean private TwitterKafkaProducer producer;

	@Test void startupApplication() {
		assertTrue(true);
	}
}
