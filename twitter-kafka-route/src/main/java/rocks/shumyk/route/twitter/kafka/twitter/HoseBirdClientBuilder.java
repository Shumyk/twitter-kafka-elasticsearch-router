package rocks.shumyk.route.twitter.kafka.twitter;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.BlockingQueue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HoseBirdClientBuilder {

	private static final Hosts hbcHosts = new HttpHosts(Constants.STREAM_HOST);

	public static Client build(final String consumerKey, final String consumerSecret,
							   final String token, final String tokenSecret,
							   final List<String> termsToTrack, final BlockingQueue<String> messageQueue) {
		final StatusesFilterEndpoint endpoint = setupEndpointPostParameters(termsToTrack);
		final OAuth1 oAuth1 = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
		return buildHosebirdClient(oAuth1, endpoint, messageQueue);
	}

	private static StatusesFilterEndpoint setupEndpointPostParameters(final List<String> terms) {
		final StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		endpoint.trackTerms(terms);
		return endpoint;
	}

	private static Client buildHosebirdClient(final OAuth1 hbcAuth,
											  final StatusesFilterEndpoint hbcEndpoint,
											  final BlockingQueue<String> messageQueue) {
		final ClientBuilder clientBuilder = new ClientBuilder()
			.name("Hosebird-Client-007")
			.hosts(hbcHosts)
			.authentication(hbcAuth)
			.endpoint(hbcEndpoint)
			.processor(new StringDelimitedProcessor(messageQueue));
		return clientBuilder.build();
	}
}
