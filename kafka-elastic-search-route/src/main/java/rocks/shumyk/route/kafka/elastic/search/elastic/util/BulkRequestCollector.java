package rocks.shumyk.route.kafka.elastic.search.elastic.util;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class BulkRequestCollector<T> implements Collector<DocWriteRequest<T>, Set<DocWriteRequest<T>>, BulkRequest> {

	@Override
	public Supplier<Set<DocWriteRequest<T>>> supplier() {
		return HashSet::new;
	}

	@Override
	public BiConsumer<Set<DocWriteRequest<T>>, DocWriteRequest<T>> accumulator() {
		return Set::add;
	}

	@Override
	public BinaryOperator<Set<DocWriteRequest<T>>> combiner() {
		return (left, right) -> {
			left.addAll(right);
			return left;
		};
	}

	@Override
	public Function<Set<DocWriteRequest<T>>, BulkRequest> finisher() {
		return set -> new BulkRequest()
			.add(set.toArray(DocWriteRequest[]::new));
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}

	public static <T> BulkRequestCollector<T> toBulkRequest() {
		return new BulkRequestCollector<>();
	}
}
