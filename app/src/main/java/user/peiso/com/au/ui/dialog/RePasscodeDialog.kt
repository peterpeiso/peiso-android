package user.peiso.com.au.ui.dialog

import android.view.View
import i.library.base.base.BaseDialogFragment
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogLoginSucceedPasscodeBinding

/**
 * Created by forever_zero on 2023/2/3.
 **/
class RePasscodeDialog(private val callback: () -> Unit)
    : BaseDialogFragment<DialogLoginSucceedPasscodeBinding>(), ClickInformBack {

    override fun getLayoutID(): Int {
        return R.layout.dialog_login_succeed_passcode
    }

    override fun initViews() {
        isCancelable = false
        binding.click = this
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iBtnConfirm -> {
                callback.invoke()
                dismiss()
            }
        }
    }
}