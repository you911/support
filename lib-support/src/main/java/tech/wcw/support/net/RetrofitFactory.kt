package tech.wcw.support.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.wcw.support.utils.LogUtils

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/3 18:20
 * @Description:
 */
class RetrofitFactory {
    companion object {
        private val retrofitFactory = RetrofitFactory()

        @JvmStatic
        fun getInstance(): RetrofitFactory {
            return retrofitFactory
        }

    }

    val TAG = "RetrofitFactory"

    var cache: HashMap<String, Retrofit> = HashMap()
    lateinit var DEFAULT_BASE_URL: String

    var logLevel = HttpLoggingInterceptor.Level.BODY

    var interceptors: List<Interceptor>? = null
    var netInterceptors: List<Interceptor>? = null

    var onError: ((throwable: Throwable) -> Unit)? =
        { throwable ->
            LogUtils.e(TAG, "------Request ERROR------")
            throwable.printStackTrace()
            LogUtils.e(TAG, "It is likely that 'RetrofitFactory.init(XXX)' was not called")
            LogUtils.e(TAG, "------Request ERROR------")
        }

    fun init(
        url: String,
        logLevel: HttpLoggingInterceptor.Level,
        interceptors: List<Interceptor> = listOf(),
        netInterceptors: List<Interceptor> = listOf(),
        onError: ((throwable: Throwable) -> Unit)? = null
    ) {
        this.DEFAULT_BASE_URL = url
        this.logLevel = logLevel
        this.interceptors = interceptors
        this.netInterceptors = netInterceptors
        onError?.let {
            this.onError = it
        }
    }

    fun getRetrofit(
        baseUrl: String = DEFAULT_BASE_URL,
        httpsEnable: Boolean = false,
    ): Retrofit {
        var retrofit = cache[baseUrl]
        if (retrofit != null) {
            return retrofit
        }
        var builder: Retrofit.Builder = Retrofit.Builder();
        var interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(
            logLevel
        )
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder
            .addInterceptor(interceptor)
            .dns(FastDNS())
        interceptors?.let {
            it.forEach { item ->
                okHttpBuilder.addInterceptor(item)
            }
        }
        netInterceptors?.let {
            it.forEach { item ->
                okHttpBuilder.addNetworkInterceptor(item)
            }
        }

        if (httpsEnable) {
            okHttpBuilder.sslSocketFactory(
                HttpsHelper.sslSocketFactory(),
                HttpsHelper.trustManager()[0]
            ).hostnameVerifier(HttpsHelper.hostnameVerifier())
        }
        val client = okHttpBuilder.build()
        retrofit = builder.baseUrl(baseUrl).client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                )
            )
            .build()
        cache[baseUrl] = retrofit
        return retrofit
    }
}