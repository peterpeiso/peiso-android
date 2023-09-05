package i.library.base.net.retrofit

import android.text.TextUtils
import i.library.base.base.BaseApplication
import i.library.base.constant.TOKEN_NAME
import i.library.base.expands.logo
import i.library.base.net.retrofit.sign.HttpDataInterceptor
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.*

/**
 * @date 2019/9/18.
 * @author hc.
 * Description: Retrofit 拦截器 - 日志
 */
class RetrofitPrintInterceptor : Interceptor{

    companion object {
        const val TAG = "PRINT_HTTP"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = initHeader(chain)
        logForRequest(request)
        val response = chain.proceed(request)
        return logForResponse(response)
    }

    /** 请求头基本参数 **/
    private fun initHeader(chain: Interceptor.Chain) : Request{
        val request = chain.request()
                .newBuilder()
        val token = BaseApplication.iGetLoginManager().iGetToken()
        if(!TextUtils.isEmpty(token)){
            request.addHeader(TOKEN_NAME, token)
        }
        request.addHeader("timeZone", TimeZone.getDefault().id)
        request.addHeader("deviceId", HttpDataInterceptor.getDeviceId())
        return request.build()
    }

    private fun logForRequest(request: Request) {
        try {
            val url = request.url().toString()
            val headers = request.headers()
            ("url : $url " + "method : " + request.method()).logo(TAG)
            if (headers.size() > 0) {
                "headers : $headers".logo(TAG)
            }
            val requestBody = request.body()
            if (requestBody != null) {
                val mediaType = requestBody.contentType()
                if (mediaType != null && !mediaType.toString().contains("multipart/form-data")) {
                    ("===== > REQUEST-CONTENT: " + bodyToString(request)).logo(TAG)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logForResponse(response: Response): Response {
        try {
            val builder = response.newBuilder()
            val clone = builder.build()
            ("url : " + clone.request().url()).logo(TAG)
            var body = clone.body()
            if (body != null) {
                val mediaType = body.contentType()
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        val resp = body.string()
                        ("responseBody's content : $resp").logo(TAG)
                        body = ResponseBody.create(mediaType, resp)
                        return response.newBuilder().body(body).build()
                    } else {
                        "PRINT TYPE ERROR".logo(TAG)
                    }
                }else{
                    "PRINT TYPE ERROR: ${clone.message()}".logo(TAG)
                }
            }else{
                "PRINT TYPE ERROR: ${clone.message()}".logo(TAG)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun isText(mediaType: MediaType): Boolean {
        return when{
            mediaType.type() == "text" ||
                    mediaType.subtype() == "json" ||
                    mediaType.subtype() == "xml"  ||
                    mediaType.subtype() == "html" ||
                    mediaType.subtype() == "webviewhtml" -> true
            else -> false
        }
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "bodyToString Error :$e"
        }
    }
}