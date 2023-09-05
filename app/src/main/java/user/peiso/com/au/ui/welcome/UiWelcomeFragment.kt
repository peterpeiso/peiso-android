package user.peiso.com.au.ui.welcome

import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.base.NullVModel
import i.library.base.expands.navigationTo
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiWelcomeBinding
import user.peiso.com.au.extend.login
import user.peiso.com.au.manager.AppLoginManager
import user.peiso.com.au.ui.MainActivity

/**
 * Created by forever_zero on 2023/1/6.
 **/
class UiWelcomeFragment: BaseFragment<UiWelcomeBinding,NullVModel>() {

    override val layoutID: Int
        get() = R.layout.ui_welcome

    override val mViewModel: NullVModel
        get() = ViewModelProvider(this)[NullVModel::class.java]

    override fun initViews() {
        login.initThis {
            when {
                MainActivity.isNeedDisposePage -> {}
                it.isLogin() -> {
                    getThisActivity<MainActivity>()?.startApp(true)
                }
                else -> {
                    getThisActivity<MainActivity>()?.startLogin(true)
                }
            }
        }
    }
}