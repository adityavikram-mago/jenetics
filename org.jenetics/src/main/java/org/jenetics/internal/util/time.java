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
package org.jenetics.internal.util;

import java.time.Duration;
import java.time.Instant;

/**
 * Helper methods for <i>time</i> classes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public class time {
    private time() {require.noInstance();}

    public static final long NANOS_PER_SECOND = 1_000_000_000;

    public static Duration minus(final Instant a, final Instant b)  {
        final long seconds = a.getEpochSecond() - b.getEpochSecond();
        final long nanos = a.getNano() - b.getNano();

        return Duration.ofNanos(seconds*NANOS_PER_SECOND + nanos);
    }
}