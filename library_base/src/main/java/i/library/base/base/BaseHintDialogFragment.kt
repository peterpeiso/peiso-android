package i.library.base.base

import android.content.DialogInterface
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener

/**
 * Created by hc. on 2021/11/16
 * Describe:
 */
abstract class BaseHintDialogFragment<VBD: ViewDataBinding> : BaseDialogFragment<VBD>() {

    class HintDialogConfig{

        lateinit var dialog: BaseHintDialogFragment<*>

        var title = ""
        var content = ""
        var contentHTML : CharSequence  = ""
        var cancel = ""
        var confirm = ""
        var link: String ?= null


        var isCanCancel = true

        var back: ((dialog : BaseHintDialogFragment<*>) -> Unit) ?= null
        var cancelBack : (() -> Unit) ?= null
        var dismissBack: (() -> Unit) ?= null

        var sizeTitle = 18f
        var sizeContent = 16f
        var sizeButton = 17f

        var gravity: Int = Gravity.CENTER

        var colorTitle: Int = 0
        var colorContent : Int = 0
        var iBackGround : Drawable ?= null
        var isConfirmDismiss = true

        var iEditSetting: ((edit: EditText) -> Unit) ?= null

        var initViews: ((BaseHintDialogFragment<*>) -> Unit) ?= null
    }

    lateinit var config : HintDialogConfig

    fun settingConfig(config : HintDialogConfig): BaseHintDialogFragment<*>{
        this.config = config
        return this
    }

    override fun initViews() {
        config.apply {
            /* init */
            isCancelable = isCanCancel
            /* more */
            iGetTextContent().gravity = gravity
            iGetTextContent().textSize = sizeContent
            if(colorContent != 0){
                iGetTextContent().setTextColor(colorContent)
            }
            if(colorTitle != 0){
                iGetTextTitle().setTextColor(colorTitle)
            }
            if(iBackGround != null){
                iGetContentView().background = iBackGround
            }
            iGetTextTitle().isVisible = !TextUtils.isEmpty(title)
            iGetTextTitle().text = title
            iGetTextTitle().textSize = sizeTitle
            if(!TextUtils.isEmpty(contentHTML)){
                iGetTextContent().text = contentHTML
            }else{
                iGetTextContent().text = content
            }
            if(colorContent != 0){
                iGetTextContent().setTextColor(colorContent)
            }
            iGetBtnCancel().isVisible = !TextUtils.isEmpty(cancel)
            iGetBtnCancel().text = cancel
            iGetBtnCancel().textSize = sizeButton
            iGetBtnCancel().setOnClickListener {
                cancelBack?.invoke()
                dismiss()
            }
            iTextLink()?.isVisible = !TextUtils.isEmpty(link)
            iTextLink()?.text = link
            iTextLink()?.paint?.flags = Paint.UNDERLINE_TEXT_FLAG
            iGetBtnConfirm().isVisible = !TextUtils.isEmpty(confirm)
            iGetBtnConfirm().text = confirm
            iGetBtnConfirm().textSize = sizeButton
            iGetBtnConfirm().setOnClickListener {
                back?.invoke(this@BaseHintDialogFragment)
                if(isConfirmDismiss)dismiss()
            }
            initViews?.invoke(this@BaseHintDialogFragment)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config.dismissBack?.invoke()
    }

    abstract fun iGetContentView(): View
    abstract fun iGetBtnCancel(): Button
    abstract fun iGetBtnConfirm(): Button
    abstract fun iGetTextTitle(): TextView
    abstract fun iGetTextContent(): TextView
    abstract fun iTextLink(): TextView?
}