package tech.wcw.support.utils

import android.annotation.TargetApi
import android.app.*
import android.app.Notification.BADGE_ICON_NONE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BADGE_ICON_NONE
import androidx.core.app.NotificationCompat.BADGE_ICON_SMALL
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import okhttp3.internal.notify
import tech.wcw.support.R

class NotifyUtils {
    companion object {
        private const val DefaultTicker = "您有一条新的消息"
        private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
        private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }


        fun newNotificationChannel(
            context: Context,
            channelId: String,
            channelName: String = "通知",
            priority: Int
        ): NotificationChannel? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    context,
                    channelId,
                    channelName,
                    priority
                )
                createNotificationGroup(
                    context,
                    channelId,
                    channelName,
                )
            }
            return null
        }

        /**
         * 创建配置通知渠道
         * @param channelId   渠道id
         * @param channelName 渠道nanme
         * @param importance  优先级
         */
        @TargetApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel(
            context: Context,
            channelId: String,
            channelName: String,
            importance: Int
        ): NotificationChannel {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.setShowBadge(false)
            channel.enableLights(true)
            channel.enableVibration(false)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.lightColor = Color.BLUE
            channel.setBypassDnd(true)
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            return channel
        }

        /**
         * 创建渠道组(若通知渠道比较多的情况下可以划分渠道组)
         * @param groupId
         * @param groupName
         */

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun createNotificationGroup(context: Context, groupId: String?, groupName: String?) {
            val group = NotificationChannelGroup(groupId, groupName)
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannelGroup(group)
        }


        /**
         * 发送通知
         *
         * @param largeIcon    大图标
         * @param smallIcon    小图标
         * @param contentTitle 标题
         * @param subText      小标题/副标题
         * @param contentText  内容
         * @param priority     优先级
         * @param ticker       通知首次弹出时，状态栏上显示的文本
         * @param notifyId     定义是否显示多条通知栏
         * @param cls          意图类
         */

        fun show(
            context: Context,
            contentTitle: String,
            contentText: String,
            channelId: String,
            subText: String? = null,
            notifyId: Int = SystemClock.uptimeMillis().toInt(),
            @DrawableRes largeIcon: Int = R.mipmap.ic_launcher,
            @DrawableRes smallIcon: Int = R.mipmap.ic_launcher,
            priority: Int = IMPORTANCE_HIGH,
            ticker: String? = DefaultTicker,
            view: RemoteViews? = null,
            channel: NotificationChannel? = newNotificationChannel(
                context,
                channelId,
                channelName = "Notify",
                priority = priority
            ),
            visibility: Int = NotificationCompat.VISIBILITY_PUBLIC,
            autoCancel: Boolean = true,
            showWhen: Boolean = true,
            badgeIconType: Int = NotificationCompat.BADGE_ICON_SMALL,
            pendingIntent: PendingIntent?
        ) {
            val manager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!openNotificationChannel(
                        context,
                        manager,
                        channelId
                    )
                ) return
            }

            //创建 NEW_MESSAGE 渠道通知栏  在API级别26.1.0中推荐使用此构造函数 Builder(context, 渠道名)
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    context,
                    channelId
                )
            builder.setChannelId(channelId)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        if (largeIcon == 0) R.mipmap.ic_launcher else largeIcon
                    )
                )
                .setSmallIcon(if (smallIcon == 0) R.mipmap.ic_launcher else smallIcon)
                .setContentText(if (TextUtils.isEmpty(contentText)) null else contentText)
                .setContentTitle(if (TextUtils.isEmpty(contentTitle)) null else contentTitle)
                .setSubText(if (TextUtils.isEmpty(subText)) null else subText)
                .setPriority(priority)
                .setTicker(if (TextUtils.isEmpty(ticker)) DefaultTicker else ticker)
                .setContent(view)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(showWhen)
                .setAutoCancel(autoCancel)
                .setVisibility(visibility)
                .setBadgeIconType(badgeIconType)
//                .setFullScreenIntent(pendingIntent, true) //悬挂式通知8.0需手动打开
            builder.setDefaults(priority)
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent) // 设置通知的点击事件
            }
            manager.notify(notifyId, builder.build()) // build()方法需要的最低API为16 ,
        }

        fun show(
            context: Context,
            notifyId: Int,
            builder: NotificationCompat.Builder,
        ) {
            val managerCompat = NotificationManagerCompat.from(context)
            managerCompat.notify(notifyId, builder.build())

        }

        /**
         * 判断应用渠道通知是否打开（适配8.0）
         * @return true 打开
         */

        fun openNotificationChannel(
            context: Context,
            manager: NotificationManagerCompat,
            channelId: String
        ): Boolean {
            //判断通知是否有打开
            if (!isNotificationEnabled(context)) {
                toNotifySetting(context, null)
                return false
            }
            //判断渠道通知是否打开
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = manager.getNotificationChannel(channelId)
                channel?.let {
                    if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                        //没打开调往设置界面
                        toNotifySetting(context, channel.id)
                        return false
                    }
                }
            }
            return true
        }

        /**
         * 判断应用通知是否打开
         * @return
         */
        fun isNotificationEnabled(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val notificationManagerCompat: NotificationManagerCompat =
                    NotificationManagerCompat.from(context)
                return notificationManagerCompat.areNotificationsEnabled()
            }
            val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var appOpsClass: Class<*>? = null
            /* Context.APP_OPS_MANAGER */try {
                appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod = appOpsClass.getMethod(
                    CHECK_OP_NO_THROW,
                    Integer.TYPE,
                    Integer.TYPE,
                    String::class.java
                )
                val opPostNotificationValue =
                    appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
                val value = opPostNotificationValue[Int::class.java] as Int
                return checkOpNoThrowMethod.invoke(
                    mAppOps,
                    value,
                    context.applicationInfo.uid,
                    context.packageName
                ) as Int == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * 手动打开应用通知
         */

        fun toNotifySetting(context: Context, channelId: String?) {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //适配 8.0及8.0以上(8.0需要先打开应用通知，再打开渠道通知)
                if (TextUtils.isEmpty(channelId)) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //适配 5.0及5.0以上
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) { // 适配 4.4及4.4以上
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.fromParts("package", context.packageName, null)
            } else {
                intent.action = Settings.ACTION_SETTINGS
            }
            context.startActivity(intent)
        }
    }
}