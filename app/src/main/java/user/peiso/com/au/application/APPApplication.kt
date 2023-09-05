package user.peiso.com.au.application

import i.library.base.base.BaseApplication
import i.library.base.listener.LoginManagerListener
import user.peiso.com.au.manager.AppLoginManager

/**
 * Created by forever_zero on 2022/12/23.
 **/
class APPApplication: BaseApplication() {

    val loginManager = AppLoginManager()

    override fun getLoginManager(): LoginManagerListener {
        return loginManager
    }

}