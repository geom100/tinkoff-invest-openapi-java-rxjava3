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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.UserContext;
import ru.tinkoff.invest.openapi.model.rest.UserAccount;

public class RxJava3UserContext {

    private static final Logger logger = LoggerFactory.getLogger(RxJava3UserContext.class);

    private Single<UserContext> context;

    public RxJava3UserContext(OpenApi openApi) {
        this.context = new RxJava3OpenApiContextProducer<UserContext>(2, () -> {
            logger.debug("Creating UserContext");
            return openApi.getUserContext();
        }).get();
    }

    /**
     * Асинхронное получение списка брокерских счетов.
     *
     * @return Список счетов.
     */
    @NotNull
    public Observable<UserAccount> getAccounts() {
        return context.flatMap(ctx -> Single.fromFuture(ctx.getAccounts()))
                .flattenAsObservable(a -> a.getAccounts());
    }
}
