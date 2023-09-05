package i.library.base.base.recycle

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import i.library.base.adapter.ItemIDInterface
import i.library.base.listener.ClickInformBack

/**
 * Created by hc. on 2020/4/22
 * Describe: 绑定视图的Adapter
 */
class BindingRecycleAdapter<T>(private val brId : Int,private val layoutId : Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    /** TYPE **/
    companion object{
        const val TYPE_ITEM = 0
        const val TYPE_HEADERS = 1
        const val TYPE_FOOTER = 2

        const val RECYCLE_MAX_NOT = -1
    }

    var iT : T ?= null

    /** 是否复用 **/
    var isRecycle = true

    /** LIST **/
    private val mList : ArrayList<T> = ArrayList()

    /** 头部 **/
    private var mHeadersView : View ?= null

    /** 尾部 **/
    private var mFooterView : View ?= null

    /** 标记 **/
    private var mTag = ""

    /** 最多显示数量 **/
    private var mMaxCount = RECYCLE_MAX_NOT

    /** 完整的列表 **/
    private val mListAll = ArrayList<T>()

    /** 点击事件 **/
    var itemClick : ((entity : T) -> Unit) ?= null

    /** 数据大小 **/
    fun getListSize() : Int{
        return mList.size
    }

    /** 获得数据 **/
    fun getDataList() : ArrayList<T>{
        return mList
    }

    /** 获取指定position的数据 **/
    fun getItemData(position : Int) : T? {
        return if(mList.size > position){
            mList[position]
        }else{
            null
        }
    }

    /** TAG **/
    fun setCVTag(tag : String){
        mTag = tag
    }

    /** HEAD **/
    fun getHeadView() : View?{
        return mHeadersView
    }

    fun getMaxCount() : Int{
        return mMaxCount
    }

    /** 刷新最大显示数量 **/
    fun refreshMaxCount(count : Int){
        mMaxCount = count
        if(count > mList.size)return
        if(count != RECYCLE_MAX_NOT){
            mListAll.clear()
            mListAll.addAll(mList)
            for (i in mList.size - 1 downTo count){
                mList.removeAt(i)
            }
            notifyItemRangeChanged(count - 1,mListAll.size)
        }else{
            if(mListAll.isEmpty())return
            val oldCount = mList.size
            for (i in mList.size until mListAll.size){
                mList.add(mListAll[i])
            }
            notifyItemRangeChanged(oldCount - 1 ,mListAll.size)
        }
    }

    fun refreshList(mList : List<T>,count : Int){
        mMaxCount = count
        refreshList(mList,false)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshUiThis(){
        notifyDataSetChanged()
    }

    /** 初始化List **/
    fun refreshList(mList : List<T>,isLoadMore : Boolean = false){
        if(!isLoadMore){
            this.mList.clear()
        }
        val currentSize = mList.size
        this.mList.addAll(mList)
        if(isLoadMore){
            notifyItemRangeChanged(currentSize,mList.size)
        }else{
            notifyDataSetChanged()
        }
        if(mMaxCount != RECYCLE_MAX_NOT){
            refreshMaxCount(mMaxCount)
        }
    }

    fun refreshListT(list: List<*>){
        refreshList(list as  List<T>)
    }

    fun refreshListRange(mList : List<T>){
        this.mList.clear()
        this.mList.addAll(mList)
        val i = this.mList.size - 1
        notifyItemRangeChanged(0,i)
        if(mMaxCount != RECYCLE_MAX_NOT){
            refreshMaxCount(mMaxCount)
        }
    }

    fun refreshItem(position: Int,item : T){
        if(mList.size > position){
            this.mList[position] = item
            notifyItemChanged(position)
        }
    }

    fun refreshItem(position: Int){
        if(mList.size > position){
            notifyItemChanged(position)
        }
    }

    fun removeItem(entity: T){
        mList.remove(entity)
        notifyDataSetChanged()
    }

    fun removeItemPosition(position: Int){
        if(mList.size > position){
            mList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun removeItemRange(position: Int,toPosition : Int = mList.size){
        if(mList.size > position || mList.size > toPosition){
            val array = ArrayList<T>()
            for(i in 0 until mList.size){
                if(i in position..toPosition){
                    array.add(mList[i])
                }
            }
            mList.removeAll(array)
            notifyItemRangeRemoved(position,toPosition)
        }
    }

    fun addItem(entity: T){
        mList.add(entity)
        notifyDataSetChanged()
    }

    fun addArray(list: List<T>){
        mList.addAll(list)
        notifyDataSetChanged()
    }

    /** 添加头布局 **/
    fun addHead(view : View){
        mHeadersView = view
        notifyDataSetChanged()
    }

    /** 是否已添加头布局 **/
    fun isHasHeadView() : Boolean{
        return mHeadersView != null
    }

    /** 添加尾布局 **/
    fun addFooter(view : View){
        mFooterView = view
    }

    /** 是否已添加头布局 **/
    fun isHasFooterView() : Boolean{
        return mFooterView != null
    }

    /** 视图绑定 **/
    var bindHolder : ((itemView : View,position: Int,entity : T) -> Unit) ?= null

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> {
                when {
                    mHeadersView != null -> TYPE_HEADERS
                    mList.isNotEmpty() -> TYPE_ITEM
                    else -> TYPE_FOOTER
                }
            }
            /** 无头部栏 **/
            itemCount - 1 -> {
                if(mFooterView != null) TYPE_FOOTER else TYPE_ITEM
            }
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_HEADERS -> {
                HeadViewHolder(mHeadersView!!)
            }
            TYPE_FOOTER -> {
                FooterViewHolder(mFooterView!!)
            }
            else -> {
                /** 绑定 **/
                val mItemBinding : ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    layoutId,
                    parent,
                    false)
                ItemViewHolder(mItemBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        var size = mList.size
        if(mHeadersView != null){ size += 1}
        if(mFooterView  != null){ size += 1}
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            val itemPosition = getPosition(position)
            if(itemPosition >= mList.size ){
                return
            }
            val entity = mList[itemPosition]
            holder.dataBinding.setVariable(brId,entity)
            val click = object : ClickInformBack {
                override fun onValidClick(v: View) {
                    itemClick?.invoke(entity)
                }
            }
            holder.itemView.setOnClickListener{
                click.onClick(it)
            }
            bindHolder?.invoke(holder.itemView,itemPosition,entity)
        }
    }

    private fun getPosition(position:Int) : Int{
        return position - (if(mHeadersView != null) 1 else 0)
    }

    override fun getItemId(position: Int): Long {
        val itemData = getItemData(position)
        return if (itemData is ItemIDInterface) {
            itemData.getItemID()
        } else {
            if (mHeadersView != null && position == 0) {
                Long.MAX_VALUE
            } else {
                super.getItemId(position)
            }
        }
    }

    /** 普通的Item **/
    class ItemViewHolder(val dataBinding : ViewDataBinding)
        : RecyclerView.ViewHolder(dataBinding.root)

    /** 头布局 **/
    class HeadViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    /** 尾布局 **/
    class FooterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
}