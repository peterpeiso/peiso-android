package i.library.base.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import i.library.base.R
import i.library.base.expands.logo
import i.library.base.manager.ControlDialogManger
import i.library.base.utils.ScreenUtils
import i.library.base.utils.ScreenUtils.dip
import i.library.base.utils.inflaterBindingView


/**
 * Created by hc. on 2021/11/8
 * Describe: DialogFragment
 */
abstract class BaseDialogFragment<VB: ViewDataBinding> : DialogFragment() {

    lateinit var binding: VB

    var iDismissBack : (() -> Unit) ?= null

    abstract fun getLayoutID() : Int

    abstract fun initViews()

    open fun getGravity() : Int = Gravity.CENTER

    open fun isMathHeight() : Boolean = false

    open fun getDAnimation() : Int? =
        when{
            getGravity() == Gravity.BOTTOM -> {
                R.style.DialogBottomAnimation
            }
            getGravity() == Gravity.CENTER && !isMathHeight() -> {
                R.style.DialogCenterAnimation
            }
            else -> {
                R.style.DialogAnimation
            }
        }

    fun setDismissBack(dismiss: () -> Unit){
        iDismissBack = dismiss
    }

    var marginX = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ControlDialogManger.addDialog(this)
        return inflaterBindingView<VB>(requireContext(),getLayoutID()).run {
            binding = this
            root.apply {
                val margin = marginEnd + marginStart
                marginX = margin
            }
        }.apply {
            val arrayOfNulls = intArrayOf(2,1)
            rootView.getLocationOnScreen(arrayOfNulls)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val params = window.attributes
        params.gravity = getGravity()
        params.width = resources.displayMetrics.widthPixels - marginX
        if(isMathHeight()){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        if (getDAnimation() != null) {
            params.windowAnimations = getDAnimation()!!
        }
        window.attributes = params
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        iDismissBack?.invoke()
        ControlDialogManger.removeDialog(this)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        }catch (e: Exception){
            e.printStackTrace()
            "dialog show error".logo()
        }
    }
}