package user.peiso.com.au.extend

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.style.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import i.library.base.adapter.IBindAdapter
import i.library.base.base.BaseApplication
import i.library.base.listener.LoginManagerListener
import i.library.base.utils.*
import i.library.base.utils.ScreenUtils.dip
import i.library.base.utils.ScreenUtils.dpf
import user.peiso.com.au.R
import user.peiso.com.au.application.APPApplication
import user.peiso.com.au.manager.AppLoginManager
import user.peiso.com.au.net.httpCheckUpdate
import user.peiso.com.au.ui.MainActivity
import user.peiso.com.au.ui.dialog.CommonDialog

/**
 * Created by forever_zero on 2022/9/23.
 **/

val application by lazy{
    BaseApplication.getInstanceT<APPApplication>()
}

val login by lazy {
    application.loginManager
}

fun iGetAlphaNavOptions(): NavOptions {
    return navOptions {
        anim {
            enter = i.library.base.R.anim.dialog_show
            exit = i.library.base.R.anim.dialog_gone
            popEnter = i.library.base.R.anim.dialog_show
            popExit = i.library.base.R.anim.dialog_gone
        }
    }
}

inline fun <reified T: ViewDataBinding> TextView.iShowWindows(
    layout: Int,
    isUseMathWidth: Boolean = true,
    callback: (binding: T,window: PopupWindow) -> Unit){
    val context = this.context
    val binding = inflaterBindingView<T>(context, layout)
    val popupWindow = PopupWindow(binding.root,
        if(isUseMathWidth) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, true)
    popupWindow.isTouchable = true
    callback.invoke(binding,popupWindow)
    popupWindow.showAsDropDown(this,0,8.dip)
}



fun String.appToMinutes(): String{
    return AMathUtils.toConvertInt(this).appToMinutes()
}

fun Int.appToMinutes(): String{
    return if(this == 1) "$this minute" else "$this minutes"
}

@BindingAdapter("classificationColor")
fun bindingClassificationColor(view: View,color: Int){
    view.background = createGradientDrawable(color,10.dpf)
}

fun Context.appWake(){
    val openIntent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(openIntent)
}

fun MainActivity.toCheckUpdate(){
    model.httpCheckUpdate{ update ->
        CommonDialog.instant().apply {
            title = "New Update Available \nV${update.version}"
            contentHTML = SpannableStringBuilder().apply {
                append("Updated contents:")
                val font = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val font = BaseApplication.getInstance().resources.getFont(R.font.medium)
                    TypefaceSpan(font)
                }else{
                    StyleSpan(Typeface.BOLD)
                }
                setSpan(font,0,length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("\n")
                append(update.updateDetails)
            }
            gravity = Gravity.START
            sizeTitle = 17f
            sizeContent = 13f
            sizeButton = 17f
            if (update.isForceAndroid == true) {
                isCanCancel = false
            } else {
                cancel = "Later"
            }
            confirm = "Update now"
            initViews = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    it.iGetTextContent().lineHeight = 27.dip
                }
            }
            back = {
                APKUtils.toMarketUpdate(this@toCheckUpdate)
            }
            dialog.show(supportFragmentManager,"Update")
        }
    }
}