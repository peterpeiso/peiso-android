package i.library.base.entity

import i.library.base.constant.HTTP_SUCCEED_COED
import okhttp3.Headers

/**
 * Created by hc. on 2021/11/10
 * Describe: 一般返回
 */
data class ApiResponse<T>(
    val code : Int,
    val message : String,
    val time : Long,
    var data : T?,
    var headers : Headers
){
    fun isSucceed(): Boolean{
        return code == HTTP_SUCCEED_COED
    }
}