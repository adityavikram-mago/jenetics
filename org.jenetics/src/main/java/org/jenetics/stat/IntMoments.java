/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.eq;

import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * {@link java.util.IntSummaryStatistics} class.
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use
 * {@link #collector(java.util.function.ToIntFunction)}  on a parallel stream,
 * because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.</i>
 *
 * @see java.util.IntSummaryStatistics
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-04-30 $</em>
 */
public class IntMoments extends Moments implements IntConsumer {

	private int _min = Integer.MAX_VALUE;
	private int _max = Integer.MIN_VALUE;
	private long _sum = 0L;

	/**
	 * Create an empty moments object.
	 */
	public IntMoments() {
	}

	/**
	 * Records a new value into the moments information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final int value) {
		super.accept(value);
		_min = min(_min, value);
		_max = max(_max, value);
		_sum += value;
	}

	/**
	 * Combine two {@code IntMoments} statistic objects.
	 *
	 * @param other the other {@code IntMoments} statistics to combine with
	 *        {@code this} one.
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public void combine(final IntMoments other) {
		super.combine(other);
		_min = min(_min, other._min);
		_max = max(_max, other._max);
		_sum += other._sum;
	}

	/**
	 * Return the minimum value recorded, or {@code Integer.MAX_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the minimum value, or {@code Integer.MAX_VALUE} if none
	 */
	public int getMin() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Integer.MIN_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the maximum value, or {@code Integer.MIN_VALUE} if none
	 */
	public int getMax() {
		return _max;
	}

	/**
	 * Return the sum of values recorded, or zero if no values have been
	 * recorded.
	 *
	 * @return the sum of values, or zero if none
	 */
	public long getSum() {
		return _sum;
	}

	@Override
	public int hashCode() {
		return Hash.of(IntMoments.class)
			.and(super.hashCode())
			.and(_min)
			.and(_max)
			.and(_sum).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == null) {
			return true;
		}
		if (!(object instanceof IntMoments)) {
			return false;
		}

		final IntMoments moments = (IntMoments)object;
		return super.equals(object) &&
			eq(_min, moments._min) &&
			eq(_max, moments._max) &&
			eq(_sum, moments._sum);
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			getCount(), _min, _max, _sum,
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an int-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * [code]
	 * final IntMoments moments = objects.stream()
	 *     .collect(IntMoments.collector(v -&gt; v.intValue()));
	 * [/code]
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, IntMoments>
	collector(final ToIntFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			IntMoments::new,
			(r, t) -> r.accept(mapper.applyAsInt(t)),
			(a, b) -> {a.combine(b); return a;}
		);
	}

}