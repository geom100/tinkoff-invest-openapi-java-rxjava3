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

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.*;

import java.time.OffsetDateTime;

public class RxJava3MarketContext {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3MarketContext.class);

    private Single<MarketContext> context;

    public RxJava3MarketContext(OpenApi openApi) {
        this.context = new RxJava3OpenApiContextProducer<MarketContext>(2, () -> {
            logger.debug("Creating MarketContext");
            return openApi.getMarketContext();
        }).get();
    }

    /**
     * Асинхронное получение списка акций, доступных для торговли.
     *
     * @return Список акций.
     */
    @NotNull
    public Observable<MarketInstrument> getMarketStocks() {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketStocks()))
                .flattenAsObservable(list -> list.getInstruments());
    }


    /**
     * Асинхронное получение бондов, доступных для торговли.
     *
     * @return Список облигаций.
     */
    @NotNull
    public Observable<MarketInstrument> getMarketBonds() {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketBonds()))
                .flattenAsObservable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение списка фондов, доступных для торговли.
     *
     * @return Список фондов.
     */
    @NotNull
    public Observable<MarketInstrument> getMarketEtfs() {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketEtfs()))
                .flattenAsObservable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение списка валют, доступных для торговли.
     *
     * @return Список валют.
     */
    @NotNull
    public Observable<MarketInstrument> getMarketCurrencies() {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketCurrencies()))
                .flattenAsObservable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение текущего состояния торгового "стакана".
     *
     * @param figi     Идентификатор инструмента.
     * @param depth    Глубина стакана.
     *
     * @return "Стакан" по инструменту или ничего, если инструмент не найден.
     */
    @NotNull
    public Maybe<Orderbook> getMarketOrderbook(@NotNull String figi, int depth) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketOrderbook(figi, depth)))
                .mapOptional(val -> val);
    }

    /**
     * Асинхронное получение исторических данных по свечам.
     *
     * @param figi     Идентификатор инструмента.
     * @param from     Начальный момент рассматриваемого отрезка временного интервала.
     * @param to       Конечный момент рассматриваемого отрезка временного интервала.
     * @param interval Разрешающий интервал свечей.
     *
     * @return Данные по свечам инструмента или ничего, если инструмент не найден.
     */
    @NotNull
    public Observable<Candle> getMarketCandles(@NotNull String figi,
                                           @NotNull OffsetDateTime from,
                                           @NotNull OffsetDateTime to,
                                           @NotNull CandleResolution interval) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getMarketCandles(figi, from, to, interval)))
                .mapOptional(val -> val).flattenAsObservable(c -> c.getCandles());
    }

    /**
     * Асинхронный поиск инструментов по тикеру.
     *
     * @param ticker Искомый тикер.
     *
     * @return Список инструментов.
     */
    @NotNull
    public Observable<MarketInstrument> searchMarketInstrumentsByTicker(@NotNull String ticker) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.searchMarketInstrumentsByTicker(ticker)))
                .flattenAsObservable(list -> list.getInstruments());
    }

    /**
     * Асинронный поиск инструмента по идентификатору.
     *
     * @param figi Искомый тикер.
     *
     * @return Найденный инструмент или ничего, если инструмент не найден.
     */
    @NotNull
    public Maybe<SearchMarketInstrument> searchMarketInstrumentByFigi(@NotNull String figi) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.searchMarketInstrumentByFigi(figi)))
                .mapOptional(val -> val);
    }
}
