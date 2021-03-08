package rocks.shumyk.kafka.streams.tweets.filter;

import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

@Slf4j
public class StreamsFilterTweets {

	public static void main(String[] args) {
		// create properties
		final Properties properties = new Properties();
		properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "tweets-filter-streams");
		properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
		properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
		properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

		// create a topology
		final StreamsBuilder streamsBuilder = new StreamsBuilder();

		// input topic -> filtered topic
		final KStream<String, String> inputTopic = streamsBuilder.stream("twitter_topics");
		final KStream<String, String> filteredStream = inputTopic.filter(
			// filter for tweets which has a user of over 10000 followers
			(key, value) -> extractUserFollowers(value) > 10_000
		);
		filteredStream.to("trended_tweets");

		// build the topology
		try (KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties)) {
			// start our streams application
			kafkaStreams.start();
		}
	}

	private static int extractUserFollowers(final String tweetJson) {
		try {
			return JsonParser.parseString(tweetJson)
				.getAsJsonObject()
				.get("user")
				.getAsJsonObject()
				.get("followers_count")
				.getAsInt();
		} catch (NullPointerException e) {
			log.error("Exception occurred during read of json tweet: {}", e.getMessage(), e);
			return 0;
		}
	}
}
