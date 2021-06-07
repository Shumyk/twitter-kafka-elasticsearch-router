package rocks.shumyk.route.kafka.elastic.search.elastic;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static rocks.shumyk.route.kafka.elastic.search.config.ConfigmapProperties.*;

@Configuration
public class ElasticSearchConfig {

	@Bean
	public RestHighLevelClient elasticSearchRestClient() {
		final var credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticUsername(), elasticPassword()));

		final var elasticRestClientBuilder = RestClient.builder(new HttpHost(elasticHostname(), 443, "https"))
			.setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider));

		return new RestHighLevelClient(elasticRestClientBuilder);
	}
}
