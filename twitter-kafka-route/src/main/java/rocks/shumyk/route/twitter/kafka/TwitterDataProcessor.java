package rocks.shumyk.route.twitter.kafka;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Arrays.asList;

@Slf4j
//@Component // todo uncomment when auth will be done
public class TwitterDataProcessor implements DataProcessor {

	private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(100_000);
	private final Hosts hbcHosts = new HttpHosts(Constants.STREAM_HOST);

	private final StatusesFilterEndpoint hbcEndpoint;
	private final Authentication hbcAuth;
	private final Client hbcClient;

	public TwitterDataProcessor() {
		// todo retrieve this auth props
		this.hbcEndpoint = setupEndpointPostParameters();
		this.hbcAuth = new OAuth1("", "", "", "");
		this.hbcClient = buildHosebirdClient();
		this.hbcClient.connect();
	}

	@PostConstruct
	public void postConstruct() throws InterruptedException {
		this.processMessages(); // todo do in different thread, maybe from constructor right away
	}

	private StatusesFilterEndpoint setupEndpointPostParameters() {
		// todo review these post parameters and probably move from hard-code to env props / config map
		final List<Long> followings = asList(453L, 4523L);
		final List<String> terms = asList("twitter", "api");

		final StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		endpoint.followings(followings);
		endpoint.trackTerms(terms);
		return endpoint;
	}

	private Client buildHosebirdClient() {
		final ClientBuilder clientBuilder = new ClientBuilder()
			.name("Hosebird-Client-007")
			.hosts(hbcHosts)
			.authentication(hbcAuth)
			.endpoint(hbcEndpoint)
			.processor(new StringDelimitedProcessor(messageQueue));
		return clientBuilder.build();
	}

	private void processMessages() throws InterruptedException {
		while (!hbcClient.isDone()) {
			final String message = messageQueue.take();
			log.info("Received message from Twitter: [{}]", message);
			produceMessageToKafka(message);
		}
	}

	private void produceMessageToKafka(final String message) {
		// todo implement in kafka producer
	}
}
