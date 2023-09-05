package i.library.base.base.recycle

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by hc. on 2020/5/28
 * Describe:
 */
fun createGridLayoutManager(context:Context, size : Int, adapter : RecyclerView.Adapter<*>) : GridLayoutManager{
    val manger = GridLayoutManager(context, size)
    manger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
        override fun getSpanSize(position: Int): Int {
            return if(adapter.getItemViewType(position) == BindingRecycleAdapter.TYPE_ITEM){
                1
            }else{
                size
            }
        }
    }
    return manger
}