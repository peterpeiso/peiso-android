package i.library.base.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import i.library.base.R
import i.library.base.constant.ACTIVITY_STATE_IMMERSIVE
import i.library.base.constant.INFORM_LOAD_END
import i.library.base.constant.INFORM_LOAD_START
import i.library.base.entity.InformEntity
import i.library.base.manager.APermissionManager
import i.library.base.manager.ControlActivityManger
import i.library.base.manager.LoadingManager
import i.library.base.utils.ScreenUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by hc. on 2021/11/8
 * Describe: Activity
 */
abstract class BaseActivity<VDB: ViewDataBinding> : AppCompatActivity(){

    lateinit var binding: VDB

    val model by lazy { ViewModelProvider(this)[NullVModel::class.java] }

    /* loading */
    private val loadingManager : LoadingManager = LoadingManager()

    /* 动态权限获取 */
    private val mAPermissionManager : APermissionManager by lazy{
        APermissionManager(this)
    }

    var thisHeight = 0

    var thisDarkState = false

    private val llContentMain: View by lazy {
        findViewById(R.id.ll_content_main)
    }

    private val flContent by lazy { 
        findViewById<FrameLayout>(R.id.fl_content) 
    }

    val ivAppBg: ImageView by lazy {
        findViewById(R.id.iv_app_bg)
    }

    var isInitAPPInform = true

    private val appInformObserver = object : Observer<InformEntity?> {
        override fun onChanged(it: InformEntity?) {
            if(isInitAPPInform) {
                isInitAPPInform = false
                return
            }
            it?:return
            appInformBasic(it)
        }
    }

    private fun appInformBasic(entity : InformEntity){
        when(entity.key){
            INFORM_LOAD_START -> {
                loadingStart(entity.value as String)
            }
            INFORM_LOAD_END -> {
                loadingFinish(entity.value as String)
            }
            else -> {
                onAPPVModelInform(entity.key,entity.value)
            }
        }
    }

    open fun onAPPVModelInform(key: String,value: Any?){ }

    open var iTitleState = -1

    abstract val layoutID : Int

    abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        /* Screen */
        ScreenUtils.setCustomDensity(this)
        /* create */
        super.onCreate(savedInstanceState)
        /* Activity */
        ControlActivityManger.addActivity(this)
        /* View */
        setContentView(R.layout.base_activity)
        /* bar */
        refreshStateBar(ACTIVITY_STATE_IMMERSIVE)
        /* init Content */
        binding = DataBindingUtil
            .inflate(LayoutInflater.from(this), layoutID, null, false)
        flContent.addView(binding.root)
        /* Observer */
        BaseApplication.getInstance().iAPPInform.observeForever(appInformObserver)
        /* init */
        initViews()
        /* height */
        flContent.postDelayed({
            if(isDestroyed)return@postDelayed
            thisHeight = flContent.height
            /* fixed height */
            flContent.layoutParams.height = flContent.height
        },200)
    }

    open fun getAppController(): NavController? {
        return null
    }

    override fun onDestroy() {
        mAPermissionManager.clear()
        BaseApplication.getInstance().iAPPInform.removeObserver(appInformObserver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        var isCanBack = true
        val fragments = supportFragmentManager.fragments
        for(f in fragments){
            if(f is BaseFragment<*,*> && isCanBack){
                isCanBack = f.onFragmentBack()
            }else if(f is NavHostFragment){
                val fragmentsX = f.childFragmentManager.fragments
                fragmentsX.forEach {
                    if(it is BaseFragment<*,*> && isCanBack){
                        isCanBack = it.onFragmentBack()
                    }
                }
            }
        }
        if(isCanBack && checkNavCanBack()){
            onBackPressedLast()
        }
    }

    open fun onChangeThisState(){ }

    open fun onBackPressedLast(){
        super.onBackPressed()
    }

    fun refreshStateTextColor(isDark : Boolean = false){
        WindowInsetsControllerCompat(window,window.decorView).apply {
            isAppearanceLightStatusBars = isDark
        }
    }

    private fun refreshStateBar(state : Int, isDark : Boolean = false){
        if(state == iTitleState) return
        WindowInsetsControllerCompat(window,window.decorView).apply {
            when(state){
                ACTIVITY_STATE_IMMERSIVE -> {
                    ViewCompat.setOnApplyWindowInsetsListener(llContentMain) { view, windowInsets ->
                        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            leftMargin = insets.left
                            bottomMargin = insets.bottom
                            rightMargin = insets.right
                        }
                        WindowInsetsCompat.CONSUMED
                    }
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.statusBarColor = Color.TRANSPARENT
                }
            }
            thisDarkState = isDark
            isAppearanceLightStatusBars = isDark
            iTitleState = state
        }
    }

    open fun loadingStart(key : String = "common"){
        lifecycleScope.launchWhenResumed{
            loadingManager.load(supportFragmentManager,true,key)
        }
    }

    open fun loadingFinish(key: String = "common"){
        lifecycleScope.launchWhenResumed {
            loadingManager.load(supportFragmentManager, false, key)
        }
    }

    fun requestAPermissions(permissions : Array<String>,
                            requestCode : Int = 0,
                            back : ((isTrue : Boolean) -> Unit)){
        mAPermissionManager.requestPermission(permissions,requestCode,back)
    }

    fun checkPermissions(permissions : Array<String>) : Boolean{
        return mAPermissionManager.isHasPermission(permissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mAPermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    /** Hide soft keyboard  */
    private fun toInputExit() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && currentFocus != null) {
            if (currentFocus!!.windowToken != null) {
                imm.hideSoftInputFromWindow(
                    currentFocus!!.windowToken,
                    0
                )
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val currentFocusX = currentFocus
        if(ev?.action == MotionEvent.ACTION_DOWN && isNeedGone(currentFocusX,ev)){
            lifecycleScope.launch {
                delay(300)
                MainScope().launch {
                    if(currentFocus == null || currentFocus !is EditText || currentFocus == currentFocusX){
                        toInputExit()
                        currentFocus?.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isNeedGone(view: View?, event: MotionEvent): Boolean{
        if(view != null && view is EditText){
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom: Int = top + view.getHeight()
            val right: Int = (left
                    + view.getWidth())
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}