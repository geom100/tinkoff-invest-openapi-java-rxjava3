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

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OperationsContext;
import ru.tinkoff.invest.openapi.model.rest.Operation;

import java.time.OffsetDateTime;

public class RxJava3OperationsContext {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3OperationsContext.class);

    private Single<OperationsContext> context;

    public RxJava3OperationsContext(OpenApi openApi) {
        this.context = new RxJava3OpenApiContextProducer<OperationsContext>(2, () -> {
            logger.debug("Creating OperationsContext");
            return openApi.getOperationsContext();
        }).get();
    }

    /**
     * Асинхронное получение списка прошедших операций по заданному инструменту за определённый промежуток времени.
     *
     * @param from Дата/время начала промежутка времени.
     * @param to Дата/время конца промежутка времени.
     * @param figi Идентификатор инструмента.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Список операций.
     */
    @NotNull
    public Observable<Operation> getOperations(@NotNull OffsetDateTime from,
                                               @NotNull OffsetDateTime to,
                                               @Nullable String figi,
                                               @Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getOperations(from, to, figi, brokerAccountId)))
                .flattenAsObservable(list -> list.getOperations());
    }
}
