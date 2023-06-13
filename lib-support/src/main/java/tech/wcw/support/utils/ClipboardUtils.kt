package tech.wcw.support.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardUtils {
    companion object {

        fun copyText(context: Context, text: CharSequence, label: String = context.packageName) {
            getManager(context).setPrimaryClip(ClipData.newPlainText(label, text))
        }

        fun clear(context: Context) {
            getManager(context).setPrimaryClip(ClipData.newPlainText(null, ""))
        }

        fun addChangedListener(
            context: Context,
            listener: ClipboardManager.OnPrimaryClipChangedListener
        ) {
            getManager(context).addPrimaryClipChangedListener(listener)
        }

        fun removeChangedListener(
            context: Context,
            listener: ClipboardManager.OnPrimaryClipChangedListener
        ) {
            getManager(context).removePrimaryClipChangedListener(listener)
        }

        private fun getManager(context: Context): ClipboardManager {
            return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }

    }

}