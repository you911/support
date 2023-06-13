package tech.wcw.support.os

import androidx.lifecycle.*
import kotlinx.coroutines.cancel
import java.lang.ref.SoftReference

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/3 17:32
 * @Description:
 */
open class BasicViewModel : ViewModel(), LifecycleEventObserver {
    lateinit var mLifecycleOwner: SoftReference<LifecycleOwner>
    val loadingState: MutableLiveData<Loading> = MutableLiveData<Loading>(Loading(null, false))
    val toast: MutableLiveData<String> = MutableLiveData()
    fun showLoading(msg: String?) {
        loadingState.postValue(Loading(msg, true))
    }

    fun dismissLoading() {
        loadingState.postValue(Loading(null, false))
    }

    fun toast(msg: String) {
        toast.postValue(msg)
    }

    inner class Loading(
        var msg: String? = null,
        var enable: Boolean = false
    )

    fun injectLifecycle(lifecycleOwner: LifecycleOwner) {
        mLifecycleOwner = SoftReference(lifecycleOwner)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                source.lifecycle.removeObserver(this)
                onDestroy()
            }
            Lifecycle.Event.ON_CREATE -> {
                onCreate()
            }
            Lifecycle.Event.ON_START -> {
                onStart()
            }
            Lifecycle.Event.ON_PAUSE -> {
                onPause()
            }
            Lifecycle.Event.ON_RESUME -> {
                onResume()
            }
            Lifecycle.Event.ON_STOP -> {
                onStop()
            }
            else -> {
                onAny()
            }
        }
    }

    open fun onAny() {

    }

    /**
     * The registered LifecycleOwner of the fragment is viewLifecycleOwner, please distinguish
     * it from the activity lifecycle of the host where the fragment is located
     * @see BaseFragment.onCreateView()
     * @see BasicViewModel.injectLifecycle()
     *
     */
    open fun onCreate() {

    }

    open fun onStart() {

    }

    open fun onPause() {

    }

    open fun onResume() {

    }

    open fun onStop() {

    }

    /**
     * The registered LifecycleOwner for fragment is viewLifecycleOwner and does not have events such as onCreate or onDestroy
     * @see BasicViewModel.injectLifecycle()
     *
     */
    open fun onDestroy() {

    }

}