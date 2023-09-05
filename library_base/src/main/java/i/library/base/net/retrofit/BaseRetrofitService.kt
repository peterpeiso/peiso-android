package i.library.base.net.retrofit

import com.google.gson.JsonElement
import i.library.base.entity.ApiResponse
import retrofit2.http.*

/**
 * Created by hc. on 2020/4/16
 * Describe: 公用的
 */
interface BaseRetrofitService {

    /**
     * 通用类型的列表请求
     * path : 路径
     * map : 请求参数
     **/
    @GET("{path}")
    suspend fun httpGetList(@Path("path",encoded = true) path : String,
                    @QueryMap  map : @JvmSuppressWildcards Map<String,Any>)
            : ApiResponse<JsonElement>

    /** 通用类型的列表请求 POST  **/
    @POST("{path}")
    suspend fun httpPostList(@Path("path",encoded = true) path : String ,
                     @Body map : @JvmSuppressWildcards HashMap<String,Any>)
            : ApiResponse<JsonElement>

    /** 通用类型的列表请求 POST - 表单  **/
    @FormUrlEncoded
    @POST("{path}")
    suspend fun httpPostListField(@Path("path",encoded = true) path : String ,
                          @FieldMap map : @JvmSuppressWildcards HashMap<String,Any>)
            : ApiResponse<JsonElement>

}