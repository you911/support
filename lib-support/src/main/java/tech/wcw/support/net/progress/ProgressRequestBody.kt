package tech.wcw.support.net.progress

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*


class ProgressRequestBody(
    val requestBody: RequestBody,
    val listener: ProgressListener?,
) : RequestBody() {
    var bufferedSink: BufferedSink? = null
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            bufferedSink = sink(sink).buffer();
        }
        bufferedSink?.let {
            requestBody.writeTo(it)
            it.flush();
        }

    }

    fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWritten = 0L
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                listener?.onProgress(
                    bytesWritten,
                    contentLength,
                    bytesWritten == contentLength
                )
            }
        }
    }
}