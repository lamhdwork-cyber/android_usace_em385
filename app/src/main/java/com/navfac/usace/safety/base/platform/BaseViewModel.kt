package com.navfac.usace.safety.base.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.navfac.usace.safety.features.bottomtab.bookmark.model.BookmarkModel
import io.reactivex.disposables.CompositeDisposable
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : AppViewModel() {
    protected val disposeBag = CompositeDisposable()
    protected val gson = Gson()

    val realmBookmark: Realm by lazy {
        Realm.open(
            RealmConfiguration.create(
                schema = setOf(
                    BookmarkModel::class
                )
            )
        )
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    fun setLoading(value: Boolean, post: Boolean = false) {
        if(post){
            _loading.postValue(value)
        }else {
            _loading.value = value
        }
    }

    private val _error = MutableLiveData<String>(null)
    val error: LiveData<String> = _error
    fun setError(value: String?) {
        _error.value = value ?: ""
    }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    fun setMessage(value: String) {
        _message.value = value
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        realmBookmark.close()
    }

    /*
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var hasError: MutableLiveData<Exception> = MutableLiveData()

    private val MAIN_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.MAIN_JOB_KEY"
    private val IO_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.IO_JOB_KEY"
    private val DEFAULT_JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.DEFAULT_JOB_KEY"

    val mainScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(MAIN_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                    MAIN_JOB_KEY,
                    CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            )
        }

    val ioScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(IO_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                    IO_JOB_KEY,
                    CloseableCoroutineScope(SupervisorJob() + Dispatchers.IO)
            )
        }

    val defaultScope: CoroutineScope
        get() {
            val scope: CoroutineScope? = this.getTag(DEFAULT_JOB_KEY)
            if (scope != null) {
                return scope
            }
            return setTagIfAbsent(
                    DEFAULT_JOB_KEY,
                    CloseableCoroutineScope(SupervisorJob() + Dispatchers.Default)
            )
        }

    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context

        override fun close() {
            coroutineContext.cancel()
        }
    }
    */
}

