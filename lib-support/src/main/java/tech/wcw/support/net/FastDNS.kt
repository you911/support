package tech.wcw.support.net

import android.text.TextUtils
import androidx.annotation.LayoutRes
import okhttp3.Dns
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/3 18:10
 * @Description:处理DNS问题；避免小米等机型连接4G时请求Api时dsn解析列表ipv6在前导致网络响应慢的问题
 */
class FastDNS : Dns {
    override fun lookup(hostname: String): MutableList<InetAddress> {
        if (TextUtils.isEmpty(hostname)) {
            throw UnknownHostException("hostname is null")
        } else {
            try {
                val mInetAddressesList = ArrayList<InetAddress>()
                val mInetAddresses =
                    InetAddress.getAllByName(hostname)
                for (address in mInetAddresses) {
                    if (address is Inet4Address) {
                        mInetAddressesList.add(0, address)
                    } else {
                        mInetAddressesList.add(address)
                    }
                }
                return mInetAddressesList
            } catch (var4: NullPointerException) {
                val unknownHostException =
                    UnknownHostException("Broken system behaviour")
                unknownHostException.initCause(var4)
                throw unknownHostException
            }
        }
    }

}