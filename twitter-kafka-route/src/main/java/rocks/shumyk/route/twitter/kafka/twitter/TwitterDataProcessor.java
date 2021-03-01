package rocks.shumyk.route.twitter.kafka.twitter;

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
import rocks.shumyk.route.twitter.kafka.kafka.TwitterKafkaProducer;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Arrays.asList;

@Slf4j
@Component
public class TwitterDataProcessor implements DataProcessor {

	private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(100_000);
	private final Hosts hbcHosts = new HttpHosts(Constants.STREAM_HOST);

	private final TwitterKafkaProducer kafkaProducer;
	private final StatusesFilterEndpoint hbcEndpoint;
	private final Client hbcClient;

	private final Authentication hbcAuth;
	// todo retrieve this auth props from env
	private final String consumerKey = "";
	private final String consumerSecret = "";
	private final String token = "";
	private final String tokenSecret = "";


	public TwitterDataProcessor(final TwitterKafkaProducer kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
		this.hbcEndpoint = setupEndpointPostParameters();
		this.hbcAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
		this.hbcClient = buildHosebirdClient();
		this.hbcClient.connect();
	}

	@PostConstruct
	public void postConstruct() throws InterruptedException {
		this.processMessages(); // todo do in different thread, maybe from constructor right away
	}

	private StatusesFilterEndpoint setupEndpointPostParameters() {
		// todo review these post parameters and probably move from hard-code to env props / config map
		final List<String> terms = asList("kafka");

		final StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
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
		int numberOfReadTweets = 0;
		while (!hbcClient.isDone() && numberOfReadTweets++ <= 50) {
			final String message = messageQueue.take();
			log.info("Received message from Twitter: [{}]", message);
			kafkaProducer.produce(message);
		}
	}
}
