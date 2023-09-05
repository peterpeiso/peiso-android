package i.library.base.base

import androidx.fragment.app.Fragment

/**
 * Created by hc. on 2020/6/3
 * Describe: 懒加载
 */
abstract class LazyFragment : Fragment(){

    var isLazyLoad = false

    fun onLazyInitViews(){
        if(!getUseLazyLoad()){
            lazyLoadData()
            isLazyLoad = true
        }
    }

    override fun onResume() {
        super.onResume()
        if(!isLazyLoad){
            lazyLoadData()
            isLazyLoad = true
        }
    }

    open fun getUseLazyLoad() : Boolean{
        return true
    }

    open fun lazyLoadData(){ }

}