package i.library.base.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import i.library.base.BuildConfig
import i.library.base.constant.INFORM_LOAD_END
import i.library.base.constant.INFORM_LOAD_START
import i.library.base.entity.BackInformEntity
import i.library.base.entity.InformEntity
import i.library.base.entity.UiStateConfig
import i.library.base.entity.UiStateConfig.Companion.I_CURRENT_STATE_CONFIG
import i.library.base.expands.NAV_LAST_START
import i.library.base.expands.logo
import i.library.base.expands.navigationTo
import i.library.base.expands.toast
import i.library.base.utils.AnimationManager
import i.library.base.utils.ScreenUtils
import kotlinx.coroutines.Job

/**
 * Created by hc. on 2021/11/8
 * Describe: Fragment
 */
abstract class BaseFragment<DB: ViewDataBinding,VM : BaseViewModel> : LazyFragment(){

    /** 适配方式 **/
    open fun iScreenAdaptWidth(): Boolean{
        return true
    }

    /** VP? **/
    open fun isViewPagerChild(): Boolean{
        return false
    }

    /** init? **/
    private var isInit = false
    var isInitView = false

    /** ViewBinding **/
    lateinit var mViewBinding : DB

    abstract val layoutID : Int

    /** 获取ViewModel **/
    abstract val mViewModel : VM

    /** 工作 **/
    private val mJobMap = HashMap<String, Job>()


    private var isParentFragment: Boolean ?= null

    var isFragmentDestroy = false

    abstract fun initViews()

    open fun initBefore(){}

    open val iThisBackKey by lazy {
        "${layoutID}_${mViewModel.javaClass}"
    }

    val binding: DB
        get() = mViewBinding

    val model: VM
        get() = mViewModel

    private var initBack = true

    private var backBundle: Bundle ?= null

    var isInitAPPInform = true

    private var iUiStateConfig: UiStateConfig ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* config */
        iUiStateConfig = differentUiStateConfig(UiStateConfig())
    }

    override fun onResume() {
        super.onResume()
        if(!isViewPagerChild()){
            checkAndRefreshTitle()
        }
    }

    private fun checkAndRefreshTitle(){
        val iUiStateConfig = iUiStateConfig?:return
        val id = iUiStateConfig.id
        val iCurrentStateConfig = I_CURRENT_STATE_CONFIG
        if(iCurrentStateConfig != id) {
            I_CURRENT_STATE_CONFIG = id
            /* title */
            refreshTitle(iUiStateConfig)
            /* background */
            getThisActivity<BaseActivity<*>>()?.ivAppBg?.apply {
                val mipmap = iUiStateConfig.backgroundMipmap
                val color = iUiStateConfig.backgroundColor
                if(mipmap != null){
                    setImageResource(mipmap)
                    AnimationManager.alphaAnimation(this,true,800L)
                }else if(color != null){
                    setImageResource(color)
                    AnimationManager.alphaAnimation(this,true,800L)
                }
            }
        }
    }

    open fun refreshTitle(config: UiStateConfig){}

    fun toPutJob(key : String,job : () -> Job ){
        if(mJobMap.containsKey(key)){
            val job1 = mJobMap[key]
            job1?.cancel()
        }
        mJobMap[key] = job.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!isInit){
            initBefore()
            initThis()
            mViewBinding = DataBindingUtil
                .inflate(inflater,layoutID,container,false)
            isInit = true
        }
        if(mViewBinding.root.background == null){
            mViewBinding.root.setBackgroundColor(Color.parseColor("#212429"))
        }
        return mViewBinding.root
    }

    private val appInformObserver = object : Observer<InformEntity?>{
        override fun onChanged(it: InformEntity?) {
            if(isInitAPPInform) {
                isInitAPPInform = false
                return
            }
            it?:return
            appInformBasic(it)
        }
    }

    private fun initThis(){
        mViewModel.mLifecycle = this
        BaseApplication.getInstance().iAPPInform.observeForever(appInformObserver)
        mViewModel.mInformLiveData.observe(viewLifecycleOwner) {
            informBasic(it)
        }
    }

    private fun appInformBasic(entity : InformEntity){
        when(entity.key){
            else -> {
                onAPPVModelInform(entity.key,entity.value)
            }
        }
    }

    fun loadingStart(key: String = ""){
        getThisActivity<BaseActivity<*>>()?.loadingStart(key)
    }

    fun loadingFinish(key: String = ""){
        getThisActivity<BaseActivity<*>>()?.loadingFinish(key)
    }

    private fun informBasic(entity : InformEntity){
        when(entity.key){
            INFORM_LOAD_START -> {
                getThisActivity<BaseActivity<*>>()?.loadingStart(entity.value as String)
            }
            INFORM_LOAD_END -> {
                getThisActivity<BaseActivity<*>>()?.loadingFinish(entity.value as String)
            }
            else -> {
                onVModelInform(entity)
            }
        }
    }

    open fun onAPPVModelInform(key: String,value: Any?){ }
    open fun onVModelInform(entity : InformEntity){ }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isInitView){
            initContentHeight()
            initViews()
            onLazyInitViews()
            isInitView = true
        }
    }

    fun checkPermissions(permissions : Array<String>) : Boolean{
        var has = false
        val activity = requireActivity()
        if(activity is BaseActivity<*>){
            has = activity.checkPermissions(permissions)
        }
        return has
    }

    fun requestAPermissions(permissions : Array<String>,
                            requestCode : Int = 0,
                            back : ((isTrue : Boolean) -> Unit)){
        val activity = requireActivity()
        if(activity is BaseActivity<*>){
            activity.requestAPermissions(permissions,requestCode,back)
        }
    }

    open fun nav() : NavController {
        return NavHostFragment.findNavController(this)
    }

    fun navExtras(id: Int, extras : Navigator.Extras?,ars : Bundle ?= null) {
        "${extras == null}".logo()
        nav().navigationTo(id,ars,null,null)
    }

    inline fun <reified T : BaseActivity<*>> getThisActivity() : T? {
        return if(requireActivity() is T){
            requireActivity() as T
        }else {
            null
        }
    }

    inline fun <reified T : BaseFragment<*,*>> getThisFragment() : T? {
        return if(parentFragment is T){
            parentFragment as T
        }else if(parentFragment is NavHostFragment){
            val parentFragmentX = parentFragment?.parentFragment
            if(parentFragmentX is T) parentFragmentX else {
                null
            }
        }else{
            null
        }
    }

    open fun onFragmentBack() : Boolean{
        return true
    }

    override fun onDestroy() {
//        if(isPageFragment()){
//            FragmentControlManger.removeFragment(this)
//        }
        BaseApplication.getInstance().iAPPInform.removeObserver(appInformObserver)
        val code = arguments?.getInt("back_result_code",0)?:0
        if(code != 0){
            val key = arguments?.getString("back_result_key")?:"KeyError"
            BaseApplication.getInstance().iBackInform.value = BackInformEntity(key,code,backBundle)
        }
        isFragmentDestroy = true
        mJobMap.forEach {
            it.value.cancel()
        }
        super.onDestroy()
    }

    private val navObserver = Observer<BackInformEntity> {
        if(initBack){
            initBack = false
            return@Observer
        }
        if(iThisBackKey != it.key){
            return@Observer
        }
        onNavResult(it.code, it.value)
    }

    fun setBackBundle(bundle: Bundle){
        backBundle = bundle
    }

    fun navNextBack(resId: Int,code: Int,args: Bundle = Bundle()){
        BaseApplication.getInstance().iBackInform.observe(this,navObserver)
        args.putInt("back_result_code",code)
        args.putString("back_result_key",iThisBackKey)
        nav().navigationTo(resId,args)
    }

    open fun onNavResult(code: Int,args: Bundle?){ }

    fun Fragment.postDelayed(delayed: Long,back: () -> Unit){
        if(view == null){
            "postDelayed: view is null".toast()
        }
        view?.postDelayed({
            if(!isFragmentDestroy){
                back.invoke()
            }
        },delayed)
    }

    open fun initContentHeight(){
        val scroll = fillContentScroll()
        scroll?.apply {
            if(childCount == 1){
                when(val child = getChildAt(0)){
                    is ConstraintLayout -> {
                        child.minHeight = ScreenUtils.getScreenWidth(requireActivity())
                    }
                }
            }
        }
    }

    open fun fillContentScroll(): ScrollView? {
        return null
    }

    open fun differentUiStateConfig(config: UiStateConfig): UiStateConfig?{
        return null
    }

    /* test */
    fun testThis(run: () -> Unit){
        if(BuildConfig.DEBUG){
            run.invoke()
        }
    }

    fun testThis(test: () -> Unit,run: () -> Unit){
        if(BuildConfig.DEBUG){
            test.invoke()
        }else{
            run.invoke()
        }
    }
}

fun checkNavCanBack() : Boolean = System.currentTimeMillis() - NAV_LAST_START > 800
