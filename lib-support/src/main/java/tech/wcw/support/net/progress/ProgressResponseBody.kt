package tech.wcw.support.net.progress

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import tech.wcw.support.net.progress.ProgressListener


class ProgressResponseBody(
    val responseBody: ResponseBody,
    val listener: ProgressListener?,
) :
    ResponseBody() {
    var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                listener?.onProgress(
                    totalBytesRead,
                    contentLength(),
                    bytesRead == -1L
                )
                return bytesRead
            }
        }
    }
}