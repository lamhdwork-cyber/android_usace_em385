package com.navfac.usace.safety.base.extension

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

fun <T> Single<T>.ioToMain(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.ioToMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> PublishSubject<T>.configureInterceptorWithEmpty(timeout: Long): Observable<T> =
        this.debounce(timeout, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
        )