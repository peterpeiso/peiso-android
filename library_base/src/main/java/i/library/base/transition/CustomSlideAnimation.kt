package i.library.base.transition

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.transition.Transition
import androidx.transition.TransitionValues
import i.library.base.expands.logo

/**
 * Created by hc. on 2021/12/27
 * Describe:
 */
class CustomSlideAnimation(val isIn: Boolean = true): Transition() {
    override fun captureStartValues(transitionValues: TransitionValues) {
        "START: captureStartValues".logo()
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        "END: captureEndValues".logo()
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        val width = sceneRoot.context.resources.displayMetrics.widthPixels
        "CREATE: createAnimator: $width".logo()
        val start = if(isIn) width else -width
        val end = if(isIn) 0 else 0
        return ValueAnimator.ofInt(start,end).apply {
            duration = 3000
            addUpdateListener {
                val layoutParams = sceneRoot.layoutParams
                if(layoutParams is ViewGroup.MarginLayoutParams){
                    layoutParams.leftMargin = it.animatedValue as Int
                    "CURRENT: createAnimator: ${it.animatedValue as Int}".logo()
                }
                sceneRoot.requestLayout()
            }
        }
    }
}