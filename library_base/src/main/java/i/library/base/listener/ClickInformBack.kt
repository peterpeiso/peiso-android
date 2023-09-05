package i.library.base.listener

import android.util.Log
import android.view.View
import i.library.base.R

/**
 * Created by hc. on 2020/4/13
 * Describe: 点击事件 - 忽略单个View的连续点击
 */
interface ClickInformBack : View.OnClickListener{

    val mKeyID : Int
        get() = R.id.click_id

    fun View.clearClickState(){
        setTag(mKeyID,0L)
    }

    /** 验证是否符合 **/
    override fun onClick(v: View?){
        v?:return
        /** 点击事件ID **/
        val tag = v.getTag(mKeyID)
        if(tag != null && tag is Long){
            val l = System.currentTimeMillis() - tag
            if(l > 500){
                v.setTag(mKeyID,System.currentTimeMillis())
                onValidClick(v)
            }else{
                Log.d("TAG-W","无效的点击事件!")
            }
        }else{
            v.setTag(mKeyID,System.currentTimeMillis())
            onValidClick(v)
        }
    }

    /** 有效的点击事件 **/
    fun onValidClick(v : View)
}