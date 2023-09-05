package user.peiso.com.au.ui.login.mobile

import android.text.TextUtils
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.expands.toast
import i.library.base.listener.ClickInformBack
import i.library.base.utils.CheckUtils
import user.peiso.com.au.BuildConfig
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiLoginMobileBinding
import user.peiso.com.au.net.httpSendCode

/**
 * Created by forever_zero on 2022/12/28.
 **/
class UiLoginMobileFragment : BaseFragment<UiLoginMobileBinding,UiLoginMobileVModel>(),
    ClickInformBack {

    override val layoutID: Int
        get() = R.layout.ui_login_mobile

    override val mViewModel: UiLoginMobileVModel
        get() = ViewModelProvider(this)[UiLoginMobileVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageBack -> {
                nav().navigateUp()
            }
            R.id.iBtnNext -> {
                val mobile = model.mobile.replace(" ", "")
                when{
                    TextUtils.isEmpty(mobile) -> {
                        "Please enter mobile number".toast()
                    }
                    !CheckUtils.checkPhoneNumber(mobile) -> {
                        "Please enter the correct mobile number".toast()
                    }
                    else -> {
                        model.httpSendCode(mobile){
                            val bundle = bundleOf("mobile" to mobile)
                            if(BuildConfig.DEBUG){
                                bundle.putString("code",it)
                            }
                            nav().navigate(R.id.fragment_login_verify,bundle)
                        }
                    }
                }
            }
        }
    }
}