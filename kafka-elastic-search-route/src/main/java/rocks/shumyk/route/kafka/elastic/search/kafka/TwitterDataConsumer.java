package rocks.shumyk.route.kafka.elastic.search.kafka;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.kafka.elastic.search.elastic.ElasticSearchPublisher;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class TwitterDataConsumer {

	private final KafkaConsumer<String, String> tweetConsumer;
	private final ElasticSearchPublisher elasticSearchPublisher;

	private final AtomicBoolean closed = new AtomicBoolean();

	public TwitterDataConsumer(final KafkaConsumer<String, String> tweetConsumer,
							   final ElasticSearchPublisher elasticSearchPublisher) {
		this.tweetConsumer = tweetConsumer;
		this.elasticSearchPublisher = elasticSearchPublisher;

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
			final ConsumerRecords<String, String> records = tweetConsumer.poll(Duration.ofMillis(100));
			if (isNull(records) || records.isEmpty()) return;
			log.info("Received {} tweet records.", records.count());

			final ImmutableMap.Builder<String, String> tweetsById = new ImmutableMap.Builder<>();
			records.forEach(record -> tweetsById.put(extractIdFromTweet(record.value()), record.value()));

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
