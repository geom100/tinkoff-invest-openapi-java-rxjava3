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
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.model.rest.*;

public class RxJava3PortfolioContext {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3PortfolioContext.class);

    private Single<PortfolioContext> context;

    public RxJava3PortfolioContext(OpenApi openApi) {
        this.context = new RxJava3OpenApiContextProducer<PortfolioContext>(2, () -> {
            logger.debug("Creating PortfolioContext");
            return openApi.getPortfolioContext();
        }).get();
    }

    /**
     * Асинхронное получение информации по портфелю инструментов.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Портфель инструментов.
     */
    @NotNull
    public Observable<PortfolioPosition> getPortfolio(@Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getPortfolio(brokerAccountId)))
                .flattenAsObservable(p -> p.getPositions());
    }

    /**
     * Асинхронное получение информации по валютным активам.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Портфель валют.
     */
    @NotNull
    public Observable<CurrencyPosition> getPortfolioCurrencies(@Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getPortfolioCurrencies(brokerAccountId)))
                .flattenAsObservable(c -> c.getCurrencies());
    }
}
