package tech.wcw.support.os

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/3 18:00
 * @Description:
 */
class LiveDataBus {
    companion object {
        private val dataCache: HashMap<String, StatefulLiveData<Any>> = HashMap()

        @Synchronized
        @JvmStatic
        fun getLiveData(channel: String): StatefulLiveData<Any> {
            if (!dataCache.containsKey(channel)) {
                val data = StatefulLiveData<Any>()
                dataCache[channel] = data
                return data
            }
            return dataCache[channel]!!
        }
    }
}