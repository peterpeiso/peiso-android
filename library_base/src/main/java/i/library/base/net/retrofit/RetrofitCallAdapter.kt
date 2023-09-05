package i.library.base.net.retrofit

import i.library.base.entity.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import okhttp3.Request
import okio.Timeout
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor


/**
 * Created by hc. on 2021/11/11
 * Describe: adapter
 */
class RetrofitCallAdapter<T>(private val callbackExecutor: Executor,
                             private val responseType: Type) : CallAdapter<T,CustomCall<T>> {

    override fun responseType(): Type = responseType
    override fun adapt(call: Call<T>): CustomCall<T> {
        return CustomCall(callbackExecutor,call)
    }

}

class CustomCall<T>(var callbackExecutor: Executor, var delegate: Call<T>) : Call<T>{

    override fun enqueue(callback: Callback<T>) {
        Objects.requireNonNull(callback, "callback == null")
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callbackExecutor.execute {
                        if (delegate.isCanceled) {
                            // Emulate OkHttp's behavior of throwing/delivering an IOException on
                            // cancellation.
                            callback.onFailure(
                                this@CustomCall,
                                IOException("Canceled")
                            )
                        } else {
                            val body = response.body()
                            if(body is ApiResponse<*>){
                                body.headers = response.headers()
                            }
                            callback.onResponse(this@CustomCall, response)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callbackExecutor.execute {
                        callback.onFailure(
                            this@CustomCall,
                            t
                        )
                    }
                }
            })
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    @Throws(IOException::class)
    override fun execute(): Response<T>? {
        return delegate.execute()
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<T> {
        return CustomCall<T>(callbackExecutor, delegate.clone())
    }

    override fun request(): Request? {
        return delegate.request()
    }

    override fun timeout(): Timeout? {
        return delegate.timeout()
    }
}