package i.library.base.listener

/**
 * Created by hc. on 2022/5/27
 * Describe:
 */
interface LoginManagerListener {

    fun iGetToken(): String

    fun iOutLogin(code: Int, isToLogin: Boolean = true)

}