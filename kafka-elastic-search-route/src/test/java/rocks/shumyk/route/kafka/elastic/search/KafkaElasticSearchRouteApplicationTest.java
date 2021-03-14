package rocks.shumyk.route.kafka.elastic.search;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SetEnvironmentVariable(key = "TOPIC_TWEETS_RAW", value = "tweets_raw")
@SetEnvironmentVariable(key = "KAFKA_HOST", value = "kafka:9092")
class KafkaElasticSearchRouteApplicationTest {

	@MockBean
	private RestHighLevelClient restHighLevelClient;
	@MockBean
	private KafkaConsumer<String, String> consumer;

	@Test
	void startup() {
		assertTrue(true);
	}
}