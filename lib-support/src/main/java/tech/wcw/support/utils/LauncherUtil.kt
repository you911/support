package tech.wcw.support.utils

import android.Manifest.permission.KILL_BACKGROUND_PROCESSES
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/4 10:02
 * @Description:
 */
class LauncherUtil {
    companion object {
        @RequiresPermission(KILL_BACKGROUND_PROCESSES)
        @JvmStatic
        fun changeIcon(
            context: Context,
            oldComponentName: ComponentName,
            newComponentName: ComponentName,
            restartNow: Boolean
        ) {
            context.packageManager.apply {
                setComponentEnabledSetting(
                    oldComponentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                setComponentEnabledSetting(
                    newComponentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                if (restartNow) {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    val resolvers = queryIntentActivities(intent, 0)
                    if (resolvers != null) {
                        for (resolver in resolvers) {
                            if (resolver.activityInfo != null) {
                                val systemService =
                                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                                systemService.killBackgroundProcesses(resolver.activityInfo.packageName)
                            }
                        }
                    }
                }
            }

        }
    }
}