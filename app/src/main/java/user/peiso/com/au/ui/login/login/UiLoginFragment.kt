package user.peiso.com.au.ui.login.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.expands.toast
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.constants.WEB_URL_POLICY
import user.peiso.com.au.constants.WEB_URL_TERMS
import user.peiso.com.au.databinding.UiLoginLoginBinding
import user.peiso.com.au.extend.login
import user.peiso.com.au.more.NoLineClickSpan
import user.peiso.com.au.ui.MainActivity
import user.peiso.com.au.ui.dialog.DeleteHintDialog
import user.peiso.com.au.ui.dialog.SelectVenueDialog
import user.peiso.com.au.ui.web.UiWebFragment

/**
 * Created by forever_zero on 2022/12/23.
 **/
class UiLoginFragment:BaseFragment<UiLoginLoginBinding, UiLoginVModel>(), ClickInformBack {

    val code by lazy {
        arguments?.getInt("code")?:0
    }

    override val layoutID: Int
        get() = R.layout.ui_login_login

    override val mViewModel: UiLoginVModel
        get() = ViewModelProvider(this)[UiLoginVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
        binding.iTextTermsPolicy.movementMethod = LinkMovementMethod.getInstance()
        binding.iTextTermsPolicy.highlightColor = Color.TRANSPARENT
        binding.iTextTermsPolicy.text = SpannableStringBuilder().apply {
            var start = length
            append("Terms of Use")
            setSpan(NoLineClickSpan{
                UiWebFragment.launch(nav(), "Terms of Use", WEB_URL_TERMS)
            }, start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            append(" | ")
            start = length
            append("Privacy Policy")
            setSpan(NoLineClickSpan {
                UiWebFragment.launch(nav(), "Privacy Policy", WEB_URL_POLICY)
            }, start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        /* check code */
        when(code){
            -1 -> {
                DeleteHintDialog().show(childFragmentManager,"Delete")
            }
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iTextForgot -> {
                nav().navigate(R.id.fragment_login_mobile)
            }
            R.id.iBtnLogin -> {
                model.httpLogin {
                    val information = login.information
                    when (information?.venueUserInfoList?.size?:0) {
                        0 -> "There are no venues associated with this account.".toast()
                        1 -> {
                            getThisActivity<MainActivity>()!!.startApp()
                        }
                        else -> {
                            SelectVenueDialog(information?.venueUserInfoList!!){ select ->
                                login.iSetSelectVenue(select)
                                getThisActivity<MainActivity>()!!.startApp()
                            }.show(childFragmentManager,"Venue")
                        }
                    }
                }
            }
        }
    }

    fun reCode(bundle: Bundle?){ }
}