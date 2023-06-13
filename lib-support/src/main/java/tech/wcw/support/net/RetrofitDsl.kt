package tech.wcw.support.net

import tech.wcw.support.BuildConfig
import tech.wcw.support.utils.LogUtils

class RetrofitDsl<ResultType> {
    val TAG: String = "RetrofitDsl"
    var api: (suspend () -> ResultType)? = null
    internal var onStart: (() -> Unit)? = null
        private set
    internal var onComplete: (() -> Unit)? = null
        private set
    internal var onSuccess: ((ResultType) -> Unit)? = null
        private set
    internal var onFailed: ((throwable: Throwable) -> Unit)? = null
        private set

    internal fun clean() {
        onSuccess = null
        onFailed = null
        onStart = null
        onComplete = null
    }

    fun onStart(block: () -> Unit) {
        this.onStart = block
    }

    fun onSuccess(block: (ResultType) -> Unit) {
        this.onSuccess = block
    }

    fun onFailed(block: (throwable: Throwable) -> Unit) {
        this.onFailed = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    fun defaultOnSuccess() {
        if (BuildConfig.DEBUG) {
            LogUtils.i(TAG, "request success!")
            LogUtils.w(TAG, "Didn't provide your own method implementation")
        }
    }

    fun defaultOnError() {
        if (BuildConfig.DEBUG) {
            LogUtils.e(TAG, "request error!")
            LogUtils.w(TAG, "Didn't provide your own method implementation")
        }
    }

}