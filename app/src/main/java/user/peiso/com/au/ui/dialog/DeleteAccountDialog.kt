package user.peiso.com.au.ui.dialog

import android.view.View
import i.library.base.base.BaseDialogFragment
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogDeleteAccountBinding

/**
 * Created by forever_zero on 2023/2/3.
 **/
class DeleteAccountDialog(private val callback: () -> Unit):
    BaseDialogFragment<DialogDeleteAccountBinding>(), ClickInformBack {

    override fun getLayoutID(): Int {
        return R.layout.dialog_delete_account
    }

    override fun initViews() {
        binding.click = this
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iBtnCancel -> {
                dismiss()
            }
            R.id.iBtnConfirm -> {
                callback.invoke()
                dismiss()
            }
        }
    }
}