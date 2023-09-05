package i.library.base.more

import android.os.Build
import android.text.TextUtils
import i.library.base.BuildConfig
import i.library.base.utils.FileUtils
import i.library.base.utils.timeGetLocal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hc. on 2020/5/20
 * Describe: 异常处理
 */
class AppUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    private var crashing : Boolean?= null
    private var mDefaultHandler : Thread.UncaughtExceptionHandler?= null

    fun init() {
        crashing = false
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", timeGetLocal())
        val date = format.format(Date(System.currentTimeMillis()))
        FileUtils.writeFile("Throwable${date}","${e?.message} : " +
                "${e.toString()} , " +
                "${t.toString()} , " +
                getCrashReport(e)
        )
        // 系统处理
        if(t != null && e != null)
            mDefaultHandler?.uncaughtException(t, e)
    }

    companion object {

        private fun getCrashReport(ex: Throwable?) : String{
            val exceptionStr = StringBuffer()
            if (ex != null) { //app版本信息
                exceptionStr.append("App Version: ? ")
                //手机系统信息
                exceptionStr.append("OS Version：" + Build.VERSION.RELEASE)
                exceptionStr.append("_")
                exceptionStr.append(Build.VERSION.SDK_INT.toString() + "\n")
                //手机制造商
                exceptionStr.append("Vendor: " + Build.MANUFACTURER + "\n")
                //手机型号
                exceptionStr.append("Model: " + Build.MODEL + "\n")
                var errorStr = ex.localizedMessage
                if (TextUtils.isEmpty(errorStr)) {
                    errorStr = ex.message
                }
                if (TextUtils.isEmpty(errorStr)) {
                    errorStr = ex.toString()
                }
                exceptionStr.append("Exception: $errorStr\n")
                val elements = ex.stackTrace
                for (i in elements.indices) {
                    exceptionStr.append(elements[i].toString() + "\n")
                }
            } else {
                exceptionStr.append("no exception. Throwable is null\n")
            }
            return exceptionStr.toString()
        }
    }

}