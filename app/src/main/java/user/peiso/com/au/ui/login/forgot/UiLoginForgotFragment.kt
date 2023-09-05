package user.peiso.com.au.ui.login.forgot

import android.view.View
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.expands.navigationTo
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiLoginForgotBinding
import user.peiso.com.au.ui.dialog.RePasscodeDialog

/**
 * Created by forever_zero on 2022/12/23.
 **/
class UiLoginForgotFragment : BaseFragment<UiLoginForgotBinding, UiLoginForgotVModel>(),
    ClickInformBack {

    override val layoutID: Int
        get() = R.layout.ui_login_forgot

    override val mViewModel: UiLoginForgotVModel
        get() = ViewModelProvider(this)[UiLoginForgotVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iBtnConfirm -> {
                model.httpResetPassword(
                    requireArguments().getString("code")?:"",
                    requireArguments().getString("mobile")?:""){
                    RePasscodeDialog{
                        nav().popBackStack(R.id.fragment_login,false)
                    }.show(childFragmentManager,"")
                }
            }
        }
    }
}