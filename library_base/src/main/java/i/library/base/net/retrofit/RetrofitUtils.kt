package i.library.base.net.retrofit

import android.os.Build
import com.google.gson.GsonBuilder
import i.library.base.net.retrofit.sign.HttpDataInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * Created by hc. on 2021/11/8
 * Describe: Retrofit
 */
object RetrofitUtils {

    /** Retrofit唯一常驻实例 **/
    var mRetrofit : Retrofit ?= null

    var lastUrl = ""

    /**
     * 获取Retrofit对象
     * isUseInterceptor 是否添加拦截器
     **/
    fun getRetrofit(url : String,
                    isUseInterceptor : Boolean = true,
                    time : Long = 30) : Retrofit{
        val retrofit = when {
            !isUseInterceptor -> {
                createRetrofit(url,false,time)
            }
            url == lastUrl -> {
                mRetrofit ?: createRetrofit(url,true,time)
            }
            else -> createRetrofit(url,true,time)
        }
        lastUrl = url
        return retrofit
    }

    private fun createRetrofit(url : String,isUseInterceptor: Boolean,time : Long) : Retrofit{
        val okHttp = createOkHttpClient(isUseInterceptor,time)
        val create = GsonBuilder().serializeNulls().create()
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttp)
            .addCallAdapterFactory(RetrofitCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(create))
            .build()
    }

    private fun createOkHttpClient(isUseInterceptor: Boolean,time : Long) : OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(time, TimeUnit.SECONDS)
            .readTimeout(time, TimeUnit.SECONDS)
            .writeTimeout(time, TimeUnit.SECONDS)
        /** 拦截器 **/
        if(isUseInterceptor){
            builder.addInterceptor(RetrofitPrintInterceptor())
            builder.addInterceptor(HttpDataInterceptor())
        }
        /** 设置忽略证书校验 **/
        try {
            val factory = getSSLSocketFactory()
            builder.sslSocketFactory(factory)
            builder.hostnameVerifier { _, _ ->
                true
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
        return builder.build()
    }


    private fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, getTrustManager(), SecureRandom())
        return sslContext.socketFactory
    }

    private fun getTrustManager(): Array<TrustManager> {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            arrayOf(
                object : X509ExtendedTrustManager() {
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?,
                        socket: Socket?
                    ) {}

                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?,
                        engine: SSLEngine?
                    ) {}

                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?,
                        socket: Socket?
                    ) {}

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?,
                        engine: SSLEngine?
                    ) {}

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }
            )
        }else{
            arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }
            )
        }
    }
}