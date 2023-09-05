package user.peiso.com.au.ui.dialog

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import i.library.base.base.BaseHintDialogFragment
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogCommonBinding

/**
 * Created by hc. on 2022/5/31
 * Describe:
 */
class CommonDialog: BaseHintDialogFragment<DialogCommonBinding>(){

    companion object{
        fun instant(): HintDialogConfig{
            return HintDialogConfig().apply {
                dialog = CommonDialog()
                dialog.config = this
            }
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_common
    }

    override fun iGetContentView(): View {
        return binding.iTextContent
    }

    override fun iGetBtnCancel(): Button {
        return binding.iBtnCancel
    }

    override fun iGetBtnConfirm(): Button {
        return binding.iBtnConfirm
    }

    override fun iGetTextTitle(): TextView {
        return binding.iTextTitle
    }

    override fun iGetTextContent(): TextView {
        return binding.iTextContent
    }

    override fun iTextLink(): TextView? {
        return null
    }
}