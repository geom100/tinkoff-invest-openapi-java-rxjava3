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

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxContext;
import ru.tinkoff.invest.openapi.model.rest.SandboxAccount;
import ru.tinkoff.invest.openapi.model.rest.SandboxRegisterRequest;
import ru.tinkoff.invest.openapi.model.rest.SandboxSetCurrencyBalanceRequest;
import ru.tinkoff.invest.openapi.model.rest.SandboxSetPositionBalanceRequest;

public class RxJava3SandboxContext {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3SandboxContext.class);

    private Single<SandboxContext> context;

    public RxJava3SandboxContext(OpenApi openApi) {
        this.context = new RxJava3OpenApiContextProducer<SandboxContext>(2, () -> {
            logger.debug("Creating SandboxContext");
            return openApi.getSandboxContext();
        }).get();
    }

    /**
     * Регистрация в системе "песочницы". Проводится один раз для клиента.
     *
     * @param registerRequest Параметры запроса.
     *
     * @return "Песочный" брокерский счёт.
     */
    @NotNull
    public Single<SandboxAccount> performRegistration(@NotNull SandboxRegisterRequest registerRequest) {
        return context.flatMap(ctx -> Single.fromFuture(ctx.performRegistration(registerRequest)));
    }

    /**
     * Установка значения валютного актива.
     *
     * @param balanceRequest Параметры запроса.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Ничего.
     */
    @NotNull
    public Completable setCurrencyBalance(@NotNull SandboxSetCurrencyBalanceRequest balanceRequest,
                                          @Nullable String brokerAccountId) {
        return context.flatMapCompletable(ctx -> Completable.fromFuture(ctx.setCurrencyBalance(balanceRequest, brokerAccountId)));
    }

    /**
     * Установка позиции по инструменту.
     *
     * @param balanceRequest Параметры запроса.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Ничего.
     */
    @NotNull
    public Completable setPositionBalance(@NotNull SandboxSetPositionBalanceRequest balanceRequest,
                                               @Nullable String brokerAccountId) {
        return context.flatMapCompletable(ctx -> Completable.fromFuture(ctx.setPositionBalance(balanceRequest, brokerAccountId)));
    }

    /**
     * Удаление "песочного" брокерского счёта.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Ничего.
     */
    @NotNull
    public Completable removeAccount(@Nullable String brokerAccountId) {
        return context.flatMapCompletable(ctx -> Completable.fromFuture(ctx.removeAccount(brokerAccountId)));
    }

    /**
     * Сброс всех установленных значений по активам.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Ничего.
     */
    @NotNull
    public Completable clearAll(@Nullable String brokerAccountId) {
        return context.flatMapCompletable(ctx -> Completable.fromFuture(ctx.clearAll(brokerAccountId)));
    }
}
