package tech.wcw.support.os

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/3 18:20
 * @Description:自动解绑Handler
 */
class LifecycleHandler : Handler, LifecycleObserver, LifecycleEventObserver {
    var lifecycleOwner: LifecycleOwner

    constructor(lifecycleOwner: LifecycleOwner, looper: Looper) : super(looper) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    constructor(lifecycleOwner: LifecycleOwner, looper: Looper, callback: Callback?) : super(
        looper,
        callback
    ) {
        this.lifecycleOwner = lifecycleOwner
        addObserver()
    }

    private fun addObserver() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_DESTROY->{
                removeCallbacksAndMessages(null)
                lifecycleOwner.lifecycle.removeObserver(this)
            }
            else -> {

            }
        }
    }
}