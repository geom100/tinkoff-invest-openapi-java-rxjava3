/*
Copyright 2021 Mikhail Rumyantsev <michael.rumyantsev@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package ru.tinkoff.invest.openapi.rxjava3;

import com.google.common.util.concurrent.RateLimiter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.Context;

import java.util.concurrent.TimeUnit;

public class RxJava3OpenApiContextProducer<T extends Context> {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3OpenApiContextProducer.class);

    private RateLimiter limiter;
    private Supplier<T> contextSupplier;

    public RxJava3OpenApiContextProducer(double rateLimit, Supplier<T> contextSupplier) {
        this.limiter = RateLimiter.create(rateLimit);
        this.contextSupplier = contextSupplier;
    }

    public Single<T> get() {
            return Observable.defer(() -> {
                if (limiter.tryAcquire()) {
                    logger.debug("Acquired successfully. Thread {}", Thread.currentThread().getName());
                    return Observable.just(contextSupplier.get());
                } else {
                    logger.debug("Not acquired. Thread {}", Thread.currentThread().getName());
                    return Observable.empty();
                }
            }).firstOrError()
//                    .retryWhen(errors -> errors.map(error -> 1)
//                        // Count the number of errors.
//                        .scan(Math::addExact)
//                        .doOnNext(errorCount -> System.out.println("No. of errors: " + errorCount + " Thread " + Thread.currentThread().getName()))
//                        // Limit the maximum number of retries.
//                        .takeWhile(errorCount -> errorCount < 3)
//                        // Signal resubscribe event after some delay.
//                        .flatMapSingle(errorCount -> Single.timer(errorCount, TimeUnit.SECONDS))
//                    )
                    .retryWhen(errors -> errors.flatMapSingle(e -> Single.timer(1000, TimeUnit.MILLISECONDS)))
        ;
    }
}
