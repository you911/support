package tech.wcw.support.os

import androidx.activity.result.ActivityResultCallback

open class ResultCallBack<O> : ActivityResultCallback<O> {
    var listener: OnActivityResultListener<O>? = null;
    override fun onActivityResult(result: O) {
        listener?.let {
            it.onActivityResult(result)
            listener = null
        }
    }


}