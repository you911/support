package tech.wcw.support.net

open class Resp<T>(var code: Int, var msg: String, var data: T?) :
    ResultInterface<T> {
    override fun isSuccess(): Boolean {
        return code == 0
    }
}