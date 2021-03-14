package rocks.shumyk.route.kafka.elastic.search.elastic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.kafka.elastic.search.elastic.util.BulkRequestCollector;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticSearchPublisher {

	private final RestHighLevelClient elasticSearchRestClient;

	public void publish(final Map<String, String> messages) throws IOException {
		final BulkRequest bulkRequest = messages.entrySet()
			.stream()
			.map(e -> toIndexRequest(e.getKey(), e.getValue()))
			.collect(BulkRequestCollector.toBulkRequest());

		if (!bulkRequest.requests().isEmpty()) {
			final BulkResponse response = elasticSearchRestClient.bulk(bulkRequest, RequestOptions.DEFAULT);
			log.info("Elastic bulk insert of {} items took {}", response.getItems().length, response.getTook().getMillis());
		}
	}

	private IndexRequest toIndexRequest(final String id, final String value) {
		return new IndexRequest("twitter")
			.id(id)
			.source(value, XContentType.JSON);
	}
}
