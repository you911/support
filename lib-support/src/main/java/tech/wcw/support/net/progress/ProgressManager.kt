package tech.wcw.support.net.progress

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

class ProgressManager {

    companion object {
        private val manager: ProgressManager = ProgressManager()

        @JvmStatic
        fun getInstance(): ProgressManager {
            return manager
        }
    }

    private val requestMap: ConcurrentHashMap<String, ProgressListener> = ConcurrentHashMap()
    private val responseMap: ConcurrentHashMap<String, ProgressListener> = ConcurrentHashMap()

    fun addRequestListener(url: String, listener: ProgressListener) {
        requestMap[url] = listener
    }

    fun addResponseListener(url: String, listener: ProgressListener) {
        responseMap[url] = listener
    }

    fun clear() {
        requestMap.clear()
        responseMap.clear()
    }

    fun removeRequestListener(url: String, listener: ProgressListener) {
        requestMap.remove(url)
    }

    fun removeResponseListener(url: String, listener: ProgressListener) {
        responseMap.remove(url)
    }

    fun generateInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return wrapResponseBody(chain.proceed(wrapRequestBody(chain.request())));
            }
        }
    }

    private fun wrapResponseBody(response: Response): Response {
        val key = response.request.url.toString()
        if (!responseMap.containsKey(key)) {
            return response
        }
        val progressListener = responseMap[key]!!

        return response.newBuilder().body(
            ProgressResponseBody(
                responseBody = response.body!!,
                listener = progressListener
            )
        ).build()
    }

    private fun wrapRequestBody(request: Request): Request {
        val key = request.url.toString()
        if (!requestMap.containsKey(key)) {
            return request
        }
        val progressListener = requestMap[key]!!

        return request.newBuilder().method(
            request.method,
            ProgressRequestBody(
                requestBody = request.body!!,
                listener = progressListener
            )
        ).build()
    }
}