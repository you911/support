package tech.wcw.support.utils

import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/4 09:21
 * @Description:
 */
class DeviceUtils {
    companion object {

        fun isAppForeground(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
            for (processInfo in runningAppProcessInfoList) {
                if (processInfo.processName == context.packageName &&
                    processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                ) {
                    return true
                }
            }
            return false
        }

        fun isPhone(context: Context): Boolean {
            val tm = getTelephonyManager(context)
            return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
        }

        @RequiresPermission(READ_PHONE_STATE)
        fun getDeviceId(context: Context): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ""
            }
            val tm = getTelephonyManager(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val imei = tm.imei
                if (!TextUtils.isEmpty(imei)) return imei
                val meid = tm.meid
                return if (TextUtils.isEmpty(meid)) "" else meid
            }
            val deviceId = tm.deviceId
            if (!TextUtils.isEmpty(deviceId)) return deviceId
            return ""
        }

        @RequiresPermission(READ_PHONE_STATE)
        fun getSerial(): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return try {
                    Build.getSerial()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    ""
                }
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
        }

        @RequiresPermission(READ_PHONE_STATE)
        fun getIMEI(context: Context): String? {
            return getImeiOrMeid(context, true)
        }

        @RequiresPermission(READ_PHONE_STATE)
        fun getMEID(context: Context): String? {
            return getImeiOrMeid(context, false)
        }

        @RequiresPermission(READ_PHONE_STATE)
        fun getImeiOrMeid(context: Context, isImei: Boolean): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ""
            }
            val tm = getTelephonyManager(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return if (isImei) {
                    getMinOne(tm.getImei(0), tm.getImei(1))
                } else {
                    getMinOne(tm.getMeid(0), tm.getMeid(1))
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val ids =
                    getSystemPropertyByReflect(if (isImei) "ril.gsm.imei" else "ril.cdma.meid")
                if (!TextUtils.isEmpty(ids)) {
                    val idArr = ids.split(",").toTypedArray()
                    return if (idArr.size == 2) {
                        getMinOne(idArr[0], idArr[1])
                    } else {
                        idArr[0]
                    }
                }
                var id0 = tm.deviceId
                var id1 = ""
                try {
                    val method: Method =
                        tm.javaClass.getMethod("getDeviceId", Int::class.javaPrimitiveType)
                    id1 = method.invoke(
                        tm,
                        if (isImei) TelephonyManager.PHONE_TYPE_GSM else TelephonyManager.PHONE_TYPE_CDMA
                    ) as String
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
                if (isImei) {
                    if (id0 != null && id0.length < 15) {
                        id0 = ""
                    }
                    if (id1 != null && id1.length < 15) {
                        id1 = ""
                    }
                } else {
                    if (id0 != null && id0.length == 14) {
                        id0 = ""
                    }
                    if (id1 != null && id1.length == 14) {
                        id1 = ""
                    }
                }
                return getMinOne(id0, id1)
            } else {
                val deviceId = tm.deviceId
                if (isImei) {
                    if (deviceId != null && deviceId.length >= 15) {
                        return deviceId
                    }
                } else {
                    if (deviceId != null && deviceId.length == 14) {
                        return deviceId
                    }
                }
            }
            return ""
        }

        private fun getMinOne(s0: String?, s1: String?): String? {
            val empty0 = TextUtils.isEmpty(s0)
            val empty1 = TextUtils.isEmpty(s1)
            if (empty0 && empty1) return ""
            if (!empty0 && !empty1) {
                return if (s0!!.compareTo(s1!!) <= 0) {
                    s0
                } else {
                    s1
                }
            }
            return if (!empty0) s0 else s1
        }

        private fun getSystemPropertyByReflect(key: String): String {
            try {
                @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
                val getMethod: Method = clz.getMethod("get", String::class.java, String::class.java)
                return getMethod.invoke(clz, key, "") as String
            } catch (e: Exception) { /**/
            }
            return ""
        }

        @SuppressLint("HardwareIds")
        @RequiresPermission(READ_PHONE_STATE)
        fun getIMSI(context: Context): String? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    getTelephonyManager(context).subscriberId
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    return ""
                }
            }
            return getTelephonyManager(context).subscriberId
        }

        fun getPhoneType(context: Context): Int {
            val tm = getTelephonyManager(context)
            return tm.phoneType
        }

        /**
         * Return whether sim card state is ready.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isSimCardReady(context: Context): Boolean {
            val tm = getTelephonyManager(context)
            return tm.simState == TelephonyManager.SIM_STATE_READY
        }

        /**
         * Return the sim operator name.
         *
         * @return the sim operator name
         */
        fun getSimOperatorName(context: Context): String? {
            val tm = getTelephonyManager(context)
            return tm.simOperatorName
        }

        /**
         * Return the sim operator using mnc.
         *
         * @return the sim operator
         */
        fun getSimOperatorByMnc(context: Context): String? {
            val tm = getTelephonyManager(context)
            val operator = tm.simOperator ?: return ""
            return when (operator) {
                "46000", "46002", "46007", "46020" -> "中国移动"
                "46001", "46006", "46009" -> "中国联通"
                "46003", "46005", "46011" -> "中国电信"
                else -> operator
            }
        }


        private fun getTelephonyManager(context: Context): TelephonyManager {
            return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }
    }

}