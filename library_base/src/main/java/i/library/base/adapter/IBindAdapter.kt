package i.library.base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import i.library.base.constant.ADAPTER_TYPE_FOOTER
import i.library.base.constant.ADAPTER_TYPE_HEAD
import i.library.base.constant.ADAPTER_TYPE_ITEM
import i.library.base.expands.logo
import i.library.base.listener.ClickInformBack
import i.library.base.utils.ScreenUtils

/**
 * Created by hc. on 2021/11/15
 * Describe: bindAdapter
 */
class IBindAdapter<T,VB: ViewDataBinding>(
    private val brId : Int,
    private val layoutId : Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var itemHeadView : View?= null

    private var itemFooterView : View?= null

    private val list = ArrayList<T>()

    var bindingClick : ((entity : T) -> Unit) ?= null
    var bindingClickOrNull : ((entity : T?) -> Unit) ?= null

    var bindingPositionClick : ((position: Int, entity : T) -> Unit) ?= null

    var bindingHolder : ((binding : VB,position: Int,entity : T) -> Unit) ?= null
    var bindingHolderOrNUll : ((binding : VB,position: Int,entity : T?) -> Unit) ?= null

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapterData(list: List<T>, isMore: Boolean = false){
        if(!isMore) this.list.clear()
        val currentSize = this.list.size
        this.list.addAll(list)
        if(isMore){
            notifyItemRangeChanged(currentSize,this.list.size)
        }else{
            refreshUI()
        }
    }

    fun refreshItemUi(position: Int,isIgnoreType: Boolean = false){
        val more = when{
            isIgnoreType -> 0
            itemHeadView == null -> 0
            else -> 1
        }
        notifyItemChanged(position + more)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshUI(){
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val itemData = getItemData(position)
        return if (itemData is ItemIDInterface) {
            itemData.getItemID()
        } else {
            if (itemHeadView != null && position == 0) {
                Long.MAX_VALUE
            } else {
                super.getItemId(position)
            }
        }
    }

    private fun createHeadMargin(context: Context): View{
        val view = View(context)
        view.layoutParams = ViewGroup.LayoutParams(ScreenUtils.dip2px(24f),0)
        return view
    }

    private val holderHeadView by lazy {
        HeadViewHolder(itemHeadView!!)
    }

    private val holderFooterView by lazy {
        HeadViewHolder(itemFooterView!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ADAPTER_TYPE_HEAD -> {
                holderHeadView
            }
            ADAPTER_TYPE_FOOTER -> {
                holderFooterView
            }
            else -> {
                /** 绑定 **/
                val mItemBinding : ViewDataBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    layoutId,
                    parent,
                    false)
                ItemViewHolder(mItemBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if(holder is ItemViewHolder){
            val itemPosition = getPosition(position)
            if(itemPosition >= list.size ){
                return
            }
            val entity = list[itemPosition]
            holder.dataBinding.setVariable(brId,entity)
            val click = object : ClickInformBack {
                override fun onValidClick(v: View) {
                    bindingClick?.invoke(entity)
                    bindingClickOrNull?.invoke(entity)
                    bindingPositionClick?.invoke(position,entity)
                }
            }
            holder.itemView.setOnClickListener{
                click.onClick(it)
            }
            bindingHolder?.invoke(holder.dataBinding as VB,itemPosition,entity)
            bindingHolderOrNUll?.invoke(holder.dataBinding as VB,itemPosition,entity)
        }
    }

    override fun getItemCount(): Int {
        var size = list.size
        if(itemHeadView != null){ size += 1}
        if(itemFooterView  != null){ size += 1}
        return size
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> {
                when {
                    itemHeadView != null -> ADAPTER_TYPE_HEAD
                    list.isNotEmpty() -> ADAPTER_TYPE_ITEM
                    else -> ADAPTER_TYPE_FOOTER
                }
            }
            itemCount - 1 -> {
                if(itemFooterView != null) ADAPTER_TYPE_FOOTER else ADAPTER_TYPE_ITEM
            }
            else -> ADAPTER_TYPE_ITEM
        }
    }

    fun getDataSize() : Int = list.size

    fun getData() : ArrayList<T> = list

    fun getItemData(position: Int) : T? = if(list.size > position){
        list[position]
    }else{
        null
    }

    fun isHasFooterView(): Boolean{
        return itemFooterView != null
    }

    fun isHasHeadView() : Boolean{
        return itemHeadView != null
    }

    fun refreshTypeItem(itemView: View,type : Int = ADAPTER_TYPE_HEAD){
        when(type){
            ADAPTER_TYPE_HEAD -> itemHeadView = itemView
            else -> itemFooterView = itemView
        }
    }

    private fun getPosition(position:Int) : Int{
        return position - (if(itemHeadView != null) 1 else 0)
    }

    /** 普通的Item **/
    class ItemViewHolder(val dataBinding : ViewDataBinding)
        : RecyclerView.ViewHolder(dataBinding.root)

    /** 头布局 **/
    class HeadViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    /** 尾布局 **/
    class FooterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
}