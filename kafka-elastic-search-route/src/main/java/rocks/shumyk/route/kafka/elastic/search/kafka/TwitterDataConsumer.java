package rocks.shumyk.route.kafka.elastic.search.kafka;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.kafka.elastic.search.elastic.ElasticSearchPublisher;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitterDataConsumer {

	private final KafkaConsumer<String, String> tweetConsumer;
	private final ElasticSearchPublisher elasticSearchPublisher;

	// todo do in thread
	@PostConstruct
	private void consumeData() throws IOException {
		while (true) {
			final ConsumerRecords<String, String> records = tweetConsumer.poll(Duration.ofMillis(100));
			if (isNull(records) || records.isEmpty()) break;
			log.info("Received {} tweet records.", records.count());

			final ImmutableMap.Builder<String, String> tweetsById = new ImmutableMap.Builder<>();
			records.forEach(record -> tweetsById.put(extractIdFromTweet(record.value()), record.value()));

			elasticSearchPublisher.publish(tweetsById.build());

			log.info("Committing offsets.");
			tweetConsumer.commitSync();
			log.info("Offsets have been committed.");
		}
	}

	private String extractIdFromTweet(final String tweetJson) {
		return JsonParser.parseString(tweetJson)
			.getAsJsonObject()
			.get("id_str")
			.getAsString();
	}
}
