package user.peiso.com.au.ui.login.create

import android.view.View
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiLoginPasscodeBinding

/**
 * Created by forever_zero on 2023/1/6.
 **/
class UiLoginCreateFragment: BaseFragment<UiLoginPasscodeBinding,UiLoginCreateVModel>(),
    ClickInformBack {

    val mobile by lazy {
        arguments?.getString("mobile")!!
    }

    override val layoutID: Int
        get() = R.layout.ui_login_passcode

    override val mViewModel: UiLoginCreateVModel
        get() = ViewModelProvider(this)[UiLoginCreateVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iBtnLogin -> {
                model.httpUserPassword(mobile){
                    nav().popBackStack()
                    nav().navigate(R.id.fragment_login)
                }
            }
        }
    }
}