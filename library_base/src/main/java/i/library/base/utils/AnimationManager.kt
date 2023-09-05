package i.library.base.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.view.isVisible
import androidx.core.view.marginTop

/**
 * Created by hc. on 2021/1/26
 * Describe: 动画管理器
 */
object AnimationManager {

    private val mAnimationMap = HashMap<String,ValueAnimator>()

    private val mTranslateAnimationMap = HashMap<String,TranslateAnimation>()

    fun animationValue(start: Int, end: Int, key : String,time: Long = 500,uploading: (Int) -> Unit){
        if(mAnimationMap.containsKey(key)){
            mAnimationMap[key]?.cancel()
            mAnimationMap.remove(key)
        }
        ValueAnimator.ofInt(start,end).apply {
            duration = time
            addUpdateListener {
                val i = it.animatedValue as Int
                uploading.invoke(i)
            }
            addListener(DefaultAnimationListener(key))
            start()
        }
    }

    /*  收起 or 打开 动画 */
    fun animationChangeHeight(view : View,toInt : Int,key : String = "${view.id}",time: Long = 500){
        if(mAnimationMap.containsKey(key)){
            mAnimationMap[key]?.cancel()
            mAnimationMap.remove(key)
        }
        ValueAnimator.ofInt(view.height,toInt).apply {
            duration = time
            addUpdateListener {
                val i = it.animatedValue as Int
                view.layoutParams.height = i
                view.requestLayout()
            }
            mAnimationMap[key] = this
            addListener(object : Animator.AnimatorListener{

                override fun onAnimationRepeat(animation: Animator) { }

                override fun onAnimationEnd(animation: Animator) {
                    if(mAnimationMap.containsKey(key)){
                        mAnimationMap.remove(key)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                    if(mAnimationMap.containsKey(key)) {
                        mAnimationMap.remove(key)
                    }
                }

                override fun onAnimationStart(animation: Animator) { }

            })
            start()
        }
    }

    /*  收起 or 打开 动画 */
    fun animationChangeHeight(view : View,toInt : Int,type : Int = 0,margin: Int,key : String = "${view.id}"){
        if(mAnimationMap.containsKey(key)){
            mAnimationMap[key]?.cancel()
            mAnimationMap.remove(key)
        }
        val isOpen = view.height < toInt
        var start = view.height
        if(start == 0 ) start = 1
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val oldTMargin = layoutParams.topMargin
        val oldBMargin = layoutParams.bottomMargin
        ValueAnimator.ofInt(start,toInt).apply {
            duration = 300
            addUpdateListener {
                val i = it.animatedValue as Int
                layoutParams.height = i
                val marginX = if(isOpen){
                    ((1f * i / toInt) * margin).toInt()
                }else{
                    ((1f * i / start) * if(type == 1) oldTMargin else oldBMargin).toInt()
                }
                when (type) {
                    0 -> { }
                    -1 -> {
                        layoutParams.bottomMargin = marginX
                    }
                    1 -> {
                        layoutParams.topMargin = marginX
                    }
                }
                view.requestLayout()
            }
            mAnimationMap[key] = this
            addListener(object : Animator.AnimatorListener{

                override fun onAnimationRepeat(animation: Animator) { }

                override fun onAnimationEnd(animation: Animator) {
                    if(mAnimationMap.containsKey(key)){
                        mAnimationMap.remove(key)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                    if(mAnimationMap.containsKey(key)) {
                        mAnimationMap.remove(key)
                    }
                }

                override fun onAnimationStart(animation: Animator) { }

            })
            start()
        }
    }

    /** 补间动画 - 平移 **/
    fun animationTranslateY(view: View,
                            formFloat :Float,toFloat: Float,
                            endBack : (() -> Unit) ?= null,
                            key : String = "${view.id}",duration: Long = 300){
        if(mTranslateAnimationMap.containsKey(key)){
            mTranslateAnimationMap[key]?.cancel()
            mTranslateAnimationMap.remove(key)
        }
        val animation = TranslateAnimation(0f, 0f, formFloat, toFloat)
        animation.duration = duration
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if(mTranslateAnimationMap.containsKey(key)){
                    mTranslateAnimationMap[key]?.cancel()
                    mTranslateAnimationMap.remove(key)
                }
                endBack?.invoke()
            }
        })
        mTranslateAnimationMap["key"] = animation
        view.startAnimation(animation)
    }

    fun alphaAnimation(view: View,isShow : Boolean = !view.isVisible,duration: Long = 300){
        if(view.isVisible == isShow) return
        if(!view.isVisible) view.visibility = View.VISIBLE
        val animation = AlphaAnimation(if(isShow) 0f else 1f, if(isShow)1f else 0f)
        animation.duration = duration
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                if(isShow) {
                    view.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                if(!isShow){
                    view.visibility = View.GONE
                    view.parent?.requestLayout()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) { }
        })
        view.startAnimation(animation)
    }

    fun rotateAnimation(view: View){
        val animation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        animation.repeatCount = -1
        animation.duration = 1500
        animation.interpolator = LinearInterpolator()
        view.animation = animation
        animation.start()
    }

    fun stateAlphaAnimation(view: View,isVisibility: Boolean){
        if(view.visibility == View.VISIBLE && isVisibility
            || view.visibility == View.GONE && !isVisibility){
            return
        }
        if(isVisibility) view.visibility = View.VISIBLE
        val animation = AlphaAnimation(if(isVisibility) 0f else 1f, if(isVisibility)1f else 0f)
        animation.duration = 200
        view.startAnimation(animation)
        if(!isVisibility) view.visibility = View.GONE
    }

    fun View.animationHeightVisibility(){
        val margin = marginTop
        val height = measuredHeight

    }


    open class DefaultAnimationListener(val key: String):Animator.AnimatorListener{

        override fun onAnimationRepeat(animation: Animator) { }

        override fun onAnimationEnd(animation: Animator) {
            if(mAnimationMap.containsKey(key)){
                mAnimationMap.remove(key)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            if(mAnimationMap.containsKey(key)) {
                mAnimationMap.remove(key)
            }
        }

        override fun onAnimationStart(animation: Animator) { }

    }
}