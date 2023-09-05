package i.library.base.base.recycle

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import i.library.base.R
import i.library.base.base.BaseFragment
import i.library.base.constant.INFORM_LOAD_LIST
import i.library.base.constant.INFORM_LOAD_MORE
import i.library.base.constant.INFORM_LOAD_STATE
import i.library.base.entity.InformEntity
import i.library.base.utils.inflaterUIView
import i.library.base.utils.isUIVisibility

/**
 * Created by hc. on 2020/5/22
 * Describe: 普通列表型数据
 */
abstract class BaseRecycleFragment<BD : ViewDataBinding,VM : BaseRecycleVModel,E : Any>
    : BaseFragment<BD,VM>(){

    /** 加载状态 **/
    open var loadState = BaseRecycleVModel.LOAD_NOT

    /** 是否需要到底的说明 **/
    open var isUseBottomText = false

    /** mAdapter **/
    private var mAdapter : BindingRecycleAdapter<E>?= null

    /** 头布局 **/
    private var mHeadView : View ?= null

    /** 初始化加载 **/
    private var isInitLoadData = false

    /** 尾布局 **/
    private var mFooterView : View ?= null

    /** 列表视图 **/
    abstract fun getRecycleView() : RecyclerView

    /** 上拉刷新视图 **/
    abstract fun getSwipeRefresh() : SwipeRefreshLayout?

    /** 创建Adapter **/
    abstract fun createAdapter() : BindingRecycleAdapter<E>

    /** 获取Adapter **/
    protected fun getAdapter() : BindingRecycleAdapter<E> {
        return if(mAdapter != null){
            mAdapter!!
        }else{
            mAdapter = createAdapter()
            mAdapter!!
        }
    }

    override fun onResume() {
        super.onResume()
        if(mViewModel.isLoadUnderway){
            getSwipeRefresh()?.isRefreshing = false
            getSwipeRefresh()?.isRefreshing = true
        }else if(isInitLoadData){
            getSwipeRefresh()?.isRefreshing = false
        }
    }

    /** NULL **/
    open fun getEmptyView() : View?{
        return null
    }

    /** 每行的数量 **/
    open fun getRowItemCount() : Int{ return 2 }

    /** 是否为线性布局 **/
    open fun isLinearManager() : Boolean{ return true }

    /** 是否第一次自动加载 **/
    open fun isImmediatelyLoad() : Boolean{ return true }

    /** 是否使用加载更多的UI **/
    open fun isUesLoadMore() : Boolean{return true}

    /** 是否使用下落的动画 **/
    open fun isUseAnimation() : Boolean{return false}

    /** 布局管理局 **/
    open fun getLayoutManager() : LinearLayoutManager{
        return if(isLinearManager()){
            LinearLayoutManager(context)
        }else{
            createGridLayoutManager(requireActivity(),getRowItemCount(),getAdapter())
        }
    }

    /** 创建头布局 **/
    open fun createHeadView() : View?{
        return null
    }

    /** 头布局 **/
    private fun getHeadView() : View? {
        return if(mHeadView == null){
            mHeadView = createHeadView()
            return mHeadView
        }else mHeadView
    }

    open fun createBefore(){}

    /** 创建底部加载中视图 **/
    open fun createFooterView() : View?{
        return if(isUesLoadMore()){
            inflaterUIView(requireActivity(), R.layout.layout_recycle_footer)
        }else null
    }

    /** 获取底部加载布局 **/
    open fun getFooterView() : View? {
        return if(mFooterView == null){
            mFooterView = createFooterView()
            return mFooterView
        }else mFooterView
    }

    /** 初始化 **/
    override fun initViews() {
        getAdapter().getDataList()
        createBefore()
        initRecycle()
        getSwipeRefresh()?.setOnRefreshListener {
            initRefresh()
        }
    }

    /** 数据懒加载 **/
    override fun lazyLoadData() {
        if(isImmediatelyLoad()){
            getSwipeRefresh()?.isRefreshing = true
            getSwipeRefresh()?.postDelayed({
                if(isFragmentDestroy)return@postDelayed
                initRefresh(true)
                isInitLoadData = true
            },800)
        }
    }

    /** 初始化Recycle **/
    private fun initRecycle(){
        /** 是否开启加载更多 **/
        if(isUesLoadMore()){
            /** 滑动到底部监听 **/
            getRecycleView().addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE){
                        val lm =
                            recyclerView.layoutManager as LinearLayoutManager?
                        val totalItemCount = recyclerView.adapter!!.itemCount
                        val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                        val visibleItemCount = recyclerView.childCount
                        if (lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0
                            && getAdapter().getListSize() != 0
                            && loadState != BaseRecycleVModel.LOAD_OVER
                        ) {
                            mViewModel.loadMore()
                        }
                    }
                }
            })
        }
        /** LayoutManager **/
        getRecycleView().layoutManager = getLayoutManager()
        /** Adapter **/
        getRecycleView().adapter = getAdapter()
        /** Animation **/
        if(isUseAnimation()){
            getRecycleView().layoutAnimation =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        }
    }

    /** 初始化加载 **/
    open fun initRefresh(isDelayed : Boolean = false){
        getSwipeRefresh()?.isRefreshing = true
        if(isDelayed){
            /** 加载数据 **/
            mViewModel.initRefresh()
        }else{
            mViewModel.initRefresh()
        }
    }

    /** 刷新列表 **/
    public fun refreshList(){
        getSwipeRefresh()?.isRefreshing = true
        mViewModel.refreshList()
    }

    /** 加载更多 **/
    protected fun loadMore(){
        getSwipeRefresh()?.isRefreshing = true
        mViewModel.loadMore()
    }

    private fun refreshProgressStateList(isLoadState: Boolean = false) {
        if (!isLoadState){
            getSwipeRefresh()?.isRefreshing = false
        }
    }

    /** ViewModel 回调 **/
    override fun onVModelInform(entity: InformEntity) {
        super.onVModelInform(entity)
        when(entity.key){
            INFORM_LOAD_LIST -> {
                onLoadOver(entity.value)
            }
            INFORM_LOAD_MORE -> {
                onLoadOver(entity.value,true)
            }
            INFORM_LOAD_STATE -> {
                loadState = entity.value as Int
                if(entity.value != BaseRecycleVModel.LOAD_START) refreshProgressStateList()
                if(!isUesLoadMore()){
                    return
                }
                val footerView : View = getFooterView() ?: return
                val isShowFooter = getAdapter().getListSize() > 3
                footerView.visibility = isUIVisibility(isShowFooter)
                /** 状态显示 **/
                footerView.findViewById<View>(R.id.footer_bar_load).visibility =
                    isUIVisibility(entity.value == BaseRecycleVModel.LOAD_START)
                when(entity.value){
                    BaseRecycleVModel.LOAD_START -> {
                        footerView.findViewById<TextView>(R.id.footer_tv_state).text = getString(R.string.recycle_load)
                    }
                    BaseRecycleVModel.LOAD_END -> {
                        footerView.findViewById<TextView>(R.id.footer_tv_state).text = getString(R.string.recycle_end)
                    }
                    BaseRecycleVModel.LOAD_OVER -> {
                        footerView.postDelayed({
                            if(isFragmentDestroy){return@postDelayed}
                            if(isUseBottomText) {
                                footerView.findViewById<TextView>(R.id.footer_tv_state).text = getString(R.string.recycle_over)
                            } else {
                                footerView.findViewById<View>(R.id.ll_footer).visibility = View.GONE
                            }
                        },200)
                    }
                    else -> {
                        footerView.findViewById<TextView>(R.id.footer_tv_state).text = getString(R.string.recycle_error)
                    }
                }
            }
        }
    }

    open fun refreshAdapter(list: List<E>,isMore : Boolean){
        getAdapter().refreshList(list,isMore)
    }

    /** 数据变化 **/
    open fun onLoadOver(value : Any?,isMore : Boolean = false){
        val last = getAdapter().getListSize()
        if(!getAdapter().isHasHeadView() && getHeadView() != null){
            getAdapter().addHead(getHeadView()!!)
        }
        if(!getAdapter().isHasFooterView() && getFooterView() != null){
            getAdapter().addFooter(getFooterView()!!)
        }
        val list = value as List<E>
        refreshAdapter(list,isMore)
        checkEmpty()
        if(isMore){
            postDelayed(50){
                getAdapter().refreshItem(last - 1)
            }
        }
    }

    open fun checkEmpty(){
        if(getEmptyView() != null){
            val b = getAdapter().getListSize() == 0
            getEmptyView()?.visibility = isUIVisibility(b)
        }
    }
}