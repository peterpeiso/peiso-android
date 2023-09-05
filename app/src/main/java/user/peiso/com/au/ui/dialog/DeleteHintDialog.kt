package user.peiso.com.au.ui.dialog

import android.view.View
import i.library.base.base.BaseDialogFragment
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogLoginDeletedBinding

/**
 * Created by forever_zero on 2023/2/3.
 **/
class DeleteHintDialog(): BaseDialogFragment<DialogLoginDeletedBinding>() {

    override fun getLayoutID(): Int {
        return R.layout.dialog_login_deleted
    }

    override fun initViews() {
        binding.iBtnConfirm.setOnClickListener(object : ClickInformBack{
            override fun onValidClick(v: View) {
                dismiss()
            }
        })
    }
}