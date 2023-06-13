package tech.wcw.support.utils

import android.util.Log

class LogUtils {
    companion object {


        const val VERBOSE = 0x10
        const val DEBUG = 0x08
        const val INFO = 0x04
        const val WARN = 0x02
        const val ERROR = 0x01

        var LOG_LEVEL = VERBOSE or DEBUG or INFO or WARN or ERROR

        fun setLevel(level: Int) {
            LOG_LEVEL = level
        }

        fun v(tag: String?, msg: String?) {
            if (LOG_LEVEL.and(VERBOSE) == VERBOSE) {
                Log.v(tag, msg!!)
            }
        }

        fun d(tag: String?, msg: String?) {
            if (LOG_LEVEL.and(DEBUG) == DEBUG) {
                Log.d(tag, msg!!)
            }
        }

        fun i(tag: String?, msg: String?) {
            if (LOG_LEVEL.and(INFO) == INFO) {
                Log.i(tag, msg!!)
            }
        }

        fun w(tag: String?, msg: String?) {
            if (LOG_LEVEL.and(WARN) == WARN) {
                Log.i(tag, msg!!)
            }
        }

        fun e(tag: String?, msg: String?) {
            if (LOG_LEVEL.and(ERROR) == ERROR) {
                Log.e(tag, msg!!)
            }
        }

        fun v(msg: String?) {
            if (LOG_LEVEL.and(VERBOSE) == VERBOSE) {
                Log.v(getCallerName(), msg!!)
            }
        }

        fun d(msg: String?) {
            if (LOG_LEVEL.and(DEBUG) == DEBUG) {
                Log.d(getCallerName(), msg!!)
            }
        }

        fun i(msg: String?) {
            if (LOG_LEVEL.and(INFO) == INFO) {
                Log.i(getCallerName(), msg!!)
            }
        }

        fun w(msg: String?) {
            if (LOG_LEVEL.and(WARN) == WARN) {
                Log.w(getCallerName(), msg!!)
            }
        }

        fun e(msg: String?) {
            if (LOG_LEVEL.and(ERROR) == ERROR) {
                Log.e(getCallerName(), msg!!)
            }
        }

        /**
         * 获取调用者的类名
         */
        private fun getCallerName(): String? {
            val caller = Thread.currentThread().stackTrace[4]
            var className = caller.className // 带有包名信息
            className = className.substring(className.lastIndexOf(".") + 1)
            return className
        }

        /**
         * 描述：日志内容多的时候(超过4k)需要打印全时.
         */
        fun largeLog(str: String) {
            var str = str
            str = str.trim { it <= ' ' }
            var index = 0
            val maxLength = 4000
            var finalString: String
            while (index < str.length) {
                finalString = if (str.length <= index + maxLength) {
                    str.substring(index)
                } else {
                    str.substring(index, maxLength)
                }
                index += maxLength
                i(getCallerName(), finalString.trim { it <= ' ' })
            }
        }
    }

}