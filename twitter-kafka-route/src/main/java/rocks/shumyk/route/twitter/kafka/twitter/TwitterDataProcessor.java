package rocks.shumyk.route.twitter.kafka.twitter;

import com.twitter.hbc.core.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rocks.shumyk.route.twitter.kafka.config.ConfigmapProperties;
import rocks.shumyk.route.twitter.kafka.kafka.TwitterKafkaProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.nonNull;
import static rocks.shumyk.route.twitter.kafka.util.GeneralUtils.split;

@Slf4j
@Component
public class TwitterDataProcessor {

	private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(100_000);

	private final TwitterKafkaProducer kafkaProducer;
	private final Client hbcClient;

	public TwitterDataProcessor(final TwitterKafkaProducer kafkaProducer, final ConfigmapProperties properties) {
		this.kafkaProducer = kafkaProducer;
		this.hbcClient = HoseBirdClientBuilder.build(
			properties.twitterConsumerKey(),
			properties.twitterConsumerSecret(),
			properties.twitterToken(),
			properties.twitterTokenSecret(),
			split(properties.getApplication().get("twitter.terms-to-track")),
			messageQueue
		);

		new Thread(this::processMessages).start();
	}

	private void processMessages() {
		hbcClient.connect();
		while (!hbcClient.isDone()) {
			final String message = takeMessageSafe();

			if (nonNull(message)) {
				log.info("Received message from Twitter: [{}]", message);
				kafkaProducer.produce(message);
			}
		}
	}

	private String takeMessageSafe() {
		try {
			return messageQueue.take();
		} catch (Exception ex) {
			log.error("Exception occurred during take of message from queue: {}", ex.getMessage(), ex);
			return null;
		}
	}

	public void close() {
		hbcClient.stop();
	}
}
