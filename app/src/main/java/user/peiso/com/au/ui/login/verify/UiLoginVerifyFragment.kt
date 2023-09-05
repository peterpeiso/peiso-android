package user.peiso.com.au.ui.login.verify

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.expands.toast
import i.library.base.listener.ClickInformBack
import i.library.base.utils.TimedTaskUtils
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiLoginCodeBinding
import user.peiso.com.au.net.httpSendCode

/**
 * Created by forever_zero on 2022/12/26.
 **/
class UiLoginVerifyFragment: BaseFragment<UiLoginCodeBinding, UiLoginVerifyVModel>(),
    ClickInformBack {

    private val time = TimedTaskUtils()

    private val mobile by lazy {
        arguments?.getString("mobile")?:""
    }

    private val code by lazy {
        arguments?.getString("code")?:""
    }

    override val layoutID: Int
        get() = R.layout.ui_login_code

    override val mViewModel: UiLoginVerifyVModel
        get() = ViewModelProvider(this)[UiLoginVerifyVModel::class.java]

    override fun initViews() {
        model.code = code
        binding.click = this
        binding.model = model
        start()
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageBack -> {
                nav().navigateUp()
            }
            R.id.iBtnNext -> {
                when (model.code.length) {
                    4 -> {
                        model.httpCheckCode(mobile){
                            nav().navigate(
                                R.id.fragment_login_forgot,
                                bundleOf("mobile" to mobile,"code" to it))
                        }
                    }
                    else -> {
                        "Please enter the complete verification code".toast()
                    }
                }
            }
            R.id.iTextResend -> {
                toSendSms()
            }
        }
    }

    private fun toSendSms(){
        if(model.recode.get() == getString(R.string.resend)){
            mViewModel.httpSendCode(mobile){
                start()
            }
        }
    }

    private fun start(){
        time.timeCountDownRun(1000,60){
            if(isFragmentDestroy) return@timeCountDownRun
            if(it == 0){
                mViewModel.recode.set(getString(R.string.resend))
            }else{
                mViewModel.recode.set("${it}s")
            }
        }
    }

    override fun onDestroy() {
        time.close()
        super.onDestroy()
    }
}