package rocks.shumyk.route.kafka.elastic.search;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.kafka.elastic.search.kafka.TwitterDataConsumer;

@Slf4j
@Component
public class KafkaElasticSearchRouteShutdownHandler {

	public KafkaElasticSearchRouteShutdownHandler(final TwitterDataConsumer twitterDataConsumer,
												  final RestHighLevelClient elasticClient) {
		Runtime.getRuntime()
			.addShutdownHook(new Thread(() -> {
				try {
					log.info("Closing resources gracefully during shutdown hook [{}, {}]", twitterDataConsumer.getClass(), elasticClient.getClass());
					twitterDataConsumer.close();
					elasticClient.close();
					log.info("Closed resources during shutdown hook.");
				} catch (Exception ex) {
					log.error("Unexpected exception occurred during closing resources during shutdown hook: {}", ex.getMessage(), ex);
				}
			}));
	}
}
