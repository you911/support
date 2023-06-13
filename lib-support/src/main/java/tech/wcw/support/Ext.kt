package tech.wcw.support

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tech.wcw.support.net.RetrofitDsl
import tech.wcw.support.net.RetrofitFactory
import tech.wcw.support.os.BasicViewModel

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/5 11:15
 * @Description:
 */
fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun Context.uri2Bitmap(uri: Uri): Bitmap {
    return BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
}

fun <T> service(
    clazz: Class<T>,
    baseUrl: String = RetrofitFactory.getInstance().DEFAULT_BASE_URL,
): T {
    return RetrofitFactory.getInstance().getRetrofit(
        baseUrl = baseUrl,
        httpsEnable = true,
    ).create(clazz)
}

@JvmName("coroutineScopeRetrofit")
fun <T> CoroutineScope.retrofit(
    dsl: RetrofitDsl<T>.() -> Unit
) {
    this.launch(Dispatchers.Main) {
        val retrofitDsl = RetrofitDsl<T>()
        retrofitDsl.dsl()
        retrofitDsl.onStart?.let { it() }
        retrofitDsl.api?.let {
            flow<T> {
                emit(retrofitDsl.api!!.invoke())
            }.flowOn(Dispatchers.IO).catch { e ->
                retrofitDsl.onComplete?.let { it() }
                if (retrofitDsl.onFailed == null) {
                    retrofitDsl.defaultOnError()
                } else {
                    retrofitDsl.onFailed!!(e)
                }
            }.collect { ret ->
                retrofitDsl.onComplete?.let { it() }
                retrofitDsl.onSuccess?.let {
                    it(ret)
                }
            }
        }
    }
}

fun <T> BasicViewModel.retrofit(
    request: suspend () -> T,
    success: (T) -> Unit,
    loadingMsg: String? = null,
    error: ((Throwable) -> Unit)? = RetrofitFactory.getInstance().onError
) {
    viewModelScope.retrofit<T> {
        api = request
        onStart {
            loadingMsg?.let {
                showLoading(it)
            }
        }
        onComplete {
            loadingMsg?.let {
                dismissLoading()
            }
        }
        onFailed { throwable ->
            error?.let {
                error(throwable)
            }
        }
        onSuccess {
            success(it)
        }
    }
}