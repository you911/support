package tech.wcw.support.net.progress

interface ProgressListener {
    fun onProgress(written: Long, total: Long, done: Boolean)
}