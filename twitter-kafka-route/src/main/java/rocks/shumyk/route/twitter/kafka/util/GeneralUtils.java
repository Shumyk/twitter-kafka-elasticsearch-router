package rocks.shumyk.route.twitter.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralUtils {

	public static List<String> split(final String toSplit) {
		return Arrays.asList(toSplit.split(","));
	}
}
