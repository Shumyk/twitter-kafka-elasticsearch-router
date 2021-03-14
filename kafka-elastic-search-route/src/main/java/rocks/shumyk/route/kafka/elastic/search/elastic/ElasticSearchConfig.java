package rocks.shumyk.route.kafka.elastic.search.elastic;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

	@Bean
	public RestHighLevelClient elasticSearchRestClient() {
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
}
