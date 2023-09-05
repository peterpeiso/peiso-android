package i.library.base.net.retrofit

import i.library.base.entity.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by hc. on 2021/11/11
 * Describe: factory
 */
class RetrofitCallAdapterFactory: CallAdapter.Factory(){

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(returnType) != ApiResponse::class.java) {
            val executor = retrofit.callbackExecutor()
            return RetrofitCallAdapter<Any>(executor!!,observableType)
        }
        return null
    }

}