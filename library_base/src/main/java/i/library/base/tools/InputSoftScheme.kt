package i.library.base.tools

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import i.library.base.manager.ControlDialogManger
import i.library.base.utils.ScreenUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class InputSoftScheme {

    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var rootView: View

    fun binding(
        rootView: View,
        lifecycle: LifecycleOwner,
        isIgnoreDialog: Boolean = true,
        change: (Boolean,Int) -> Unit){
        this.isIgnoreDialog = isIgnoreDialog
        this.rootView = rootView
        this.lifecycleOwner = lifecycle
        initListener()
        iSate.observe(lifecycle){
            if(isIgnoreDialog && ControlDialogManger.isWaitHasDialog())return@observe
            change.invoke(it?:true,imeHeight)
        }
    }

    private var isIgnoreDialog = false

    /* lock */
    private var isLockState = false


    private val mScreenHeight by lazy {
        ScreenUtils.getScreenWidth(rootView.context)
    }

    private var count = 10L

    /* open or close state */
    private val iSate: MutableLiveData<Boolean?> by lazy{
        MutableLiveData(false)
    }

    /* rect */
    private val r = Rect()

    /* height */
    private var imeHeight = 0

    private fun initListener(){
        /* state change */
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            rootView.postDelayed({
                rootView.getWindowVisibleDisplayFrame(r)
                when{
                    isLockState -> {}
                    iSate.value == false && mScreenHeight > r.bottom -> {
                        val to  = mScreenHeight - r.bottom
                        imeHeight = to
                        iSetState(true)
                        startCheckHeight()
                    }
                    iSate.value == true && abs(mScreenHeight - r.bottom) < 300 -> {
                        iSetState(false)
                    }
                }
            },100)
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun startCheckHeight(){
        val mScreenHeight = ScreenUtils.getScreenWidth(rootView.context)
        lifecycleOwner.lifecycleScope.launch {
            delay(50)
            MainScope().launch {
                rootView.getWindowVisibleDisplayFrame(r)
                val to = mScreenHeight - r.bottom
                if(to == 0){
                    count = 0
                }else{
                    if(imeHeight != to && iSate.value == true){
                        count = 0
                        imeHeight = to
                        iSate.value = null
                    }
                }
                count--
                if(count > 0){
                    startCheckHeight()
                }
            }
        }
    }

    /* state */
    private fun iSetState(state: Boolean){
        if(iSate.value != state){
            iSate.value = state
        }
    }

}
