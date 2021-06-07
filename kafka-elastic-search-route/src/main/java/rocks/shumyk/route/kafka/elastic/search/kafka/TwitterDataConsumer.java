package rocks.shumyk.route.kafka.elastic.search.kafka;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.kafka.elastic.search.config.ConfigmapProperties;
import rocks.shumyk.route.kafka.elastic.search.elastic.ElasticSearchPublisher;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class TwitterDataConsumer {

	private final KafkaConsumer<String, String> tweetConsumer;
	private final ElasticSearchPublisher elasticSearchPublisher;

	private final long sleepWaitTime;
	private final AtomicBoolean closed = new AtomicBoolean();

	public TwitterDataConsumer(final KafkaConsumer<String, String> tweetConsumer,
							   final ElasticSearchPublisher elasticSearchPublisher,
							   final ConfigmapProperties properties) {
		this.tweetConsumer = tweetConsumer;
		this.elasticSearchPublisher = elasticSearchPublisher;
		this.sleepWaitTime = Long.parseLong(properties.getApplication().getOrDefault("twitter.consumer.sleep-time-seconds", "5"));

		log.info("Starting consume data from Kafka");
		new Thread(this::consumeData).start();
	}

	private void consumeData() {
		log.info("Entering while loop");
		while (!closed.get()) consumeDataSafely();
		tweetConsumer.close();
	}

	private void consumeDataSafely() {
		try {
			final var records = tweetConsumer.poll(Duration.ofMillis(100));
			if (isNull(records) || records.isEmpty()) {
				log.info("No records [{}], sleeping...", records);
				TimeUnit.SECONDS.sleep(sleepWaitTime);
				return;
			}
			log.info("Received {} tweet records.", records.count());

			final ImmutableMap.Builder<String, String> tweetsById = new ImmutableMap.Builder<>();
			records.forEach(tweet -> tweetsById.put(extractIdFromTweet(tweet.value()), tweet.value()));

			elasticSearchPublisher.publish(tweetsById.build());

			log.info("Committing offsets.");
			tweetConsumer.commitSync();
			log.info("Offsets have been committed.");
		} catch (Exception ex) {
			log.error("Exception occurred during consuming and publishing data to Elastic Search", ex);
		}
	}

	private String extractIdFromTweet(final String tweetJson) {
		return JsonParser.parseString(tweetJson)
			.getAsJsonObject()
			.get("id_str")
			.getAsString();
	}

	public void close() {
		log.info("Closing Kafka consumer");
		this.closed.set(true);
	}
}
