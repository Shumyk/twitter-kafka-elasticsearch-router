package rocks.shumyk.route.twitter.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralUtils {

	public static List<String> split(final String toSplit) {
		if (Objects.isNull(toSplit)) {
			return Collections.emptyList();
		}
		return Arrays.asList(toSplit.split(","));
	}
}
