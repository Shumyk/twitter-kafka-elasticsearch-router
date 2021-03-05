package rocks.shumyk.route.kafka.elastic.search;

import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static rocks.shumyk.route.kafka.elastic.search.TweetsKafkaConsumer.initiateKafkaConsumer;

@Slf4j
public class ElasticSearchConsumer {

	public static RestHighLevelClient createElasticSearchRestClient() {
		// todo move to properties
		final String hostname = "";
		final String username = "";
		final String password = "";

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

		final RestClientBuilder elasticRestClientBuilder = RestClient.builder(new HttpHost(hostname, 443, "https"))
			.setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider));

		return new RestHighLevelClient(elasticRestClientBuilder);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final RestHighLevelClient elasticSearchRestClient = createElasticSearchRestClient();

		final KafkaConsumer<String, String> tweetConsumer = initiateKafkaConsumer();
		while (true) {
			final ConsumerRecords<String, String> records = tweetConsumer.poll(Duration.ofMillis(100));
			log.info("Received {} tweet records.", records.count());

			for (ConsumerRecord<String, String> record : records) {
				// 2 strategies of creating ID
				// kafka generic ID
//				final String kafkaGenericId = record.topic() + "_" + record.partition() + "_" + record.offset();
				// twitter feed specific id
				final String twitterId = extractIdFromTweet(record.value());
				final IndexRequest indexRequest = new IndexRequest("twitter")
					.id(twitterId) // ensuring idempotence
					.source(record.value(), XContentType.JSON);

				final IndexResponse response = elasticSearchRestClient.index(indexRequest, RequestOptions.DEFAULT);
				final String responseId = response.getId();
				log.info("Received response with ID: {}", responseId);

				MILLISECONDS.sleep(2000);
			}

			// committing offsets
			log.info("Committing offsets.");
			tweetConsumer.commitSync();
			log.info("Offsets have been committed.");
		}

		// close the client gracefully
//		elasticSearchRestClient.close();
	}

	private static String extractIdFromTweet(final String tweetJson) {
		return JsonParser.parseString(tweetJson)
			.getAsJsonObject()
			.get("id_str")
			.getAsString();
	}
}
