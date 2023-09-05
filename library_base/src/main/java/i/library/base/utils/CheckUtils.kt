package i.library.base.utils

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * Created by hc. on 2021/11/10
 * Describe: 检查
 */
object CheckUtils {

    /** 检查密码规范 **/
    fun checkPassword(str : String) : Boolean{
        return !TextUtils.isEmpty(str) && (str.length >= 8)
    }

    /** 检查手机号码规范 **/
    fun checkPhoneNumber(str : String) : Boolean{
        val m = str.replace(" ", "")
        return ((m.startsWith("04") && m.length == 10) ||
                (m.startsWith("4") && m.length == 9))
    }

    /** 检查邮箱格式规范 **/
    fun checkEmail(str : String) : Boolean{
        val regex =
            "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(str)
        return matcher.matches()
    }

    /* abn */
    fun checkABN(str: String?): Boolean{
        return str?.length == 11
    }

    /* card bsb */
    fun checkCardBSB(str: String?): Boolean{
        return str?.length == 6
    }

    /* card number */
    fun checkCardNumber(str: String?): Boolean{
        return (str?.length?:0) >= 8 && (str?.length?:0) <= 16
    }

    /* tax number */
    fun checkTaxFileNumber(str: String?): Boolean{
        return str?.length == 9
    }

    /** 规范号码显示 **/
    fun formatPhone(mobile: String?,isAddBlank : Boolean = true) : String{
        mobile?:return ""
        val str = mobile.replace(" ","")
        when{
            (str.length == 9 && str.startsWith("4") ) ||
                    (str.length == 10 && str.startsWith("04"))-> {
                val m1 =  if(!mobile.startsWith("0")){
                    "0$mobile".replace(" ","")
                }else{
                    mobile.replace(" ","")
                }
                return if(isAddBlank && m1.length > 9){
                    m1.substring(0, 4) + " " + m1.substring(4, 7) + " " + m1.substring(7, m1.length)
                }else{
                    "$mobile"
                }
            }
            str.length == 11 -> {
                return str.substring(0,3) + " " + str.substring(3,7) + " " + str.substring(7, str.length)
            }
            else -> {
                return mobile
            }
        }
    }
}