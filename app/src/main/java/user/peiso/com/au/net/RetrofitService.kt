package user.peiso.com.au.net

import i.library.base.entity.ApiResponse
import i.library.base.net.retrofit.RetrofitUtils
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import user.peiso.com.au.BuildConfig
import user.peiso.com.au.entity.*

/**
 * Created by forever_zero on 2022/11/1.
 **/
interface RetrofitService {

    companion object{

        var isHttpRelease = false
        private const val F = "https://sandbox-app.peiso.com.au/"
        private const val T = "https://app.peiso.com.au/"

        private fun iGetCurrentUrl(): String{
            return if(BuildConfig.DEBUG) {
               if (isHttpRelease) {
                    T
                } else {
                    F
                }
            }else {
                T
            }
        }

        fun instant(): RetrofitService {
            return RetrofitUtils.getRetrofit(iGetCurrentUrl()).create(RetrofitService::class.java)
        }

    }

    /**
     * mobileNumber
     * password
     */
    @POST("/v1/api-app/user-login")
    suspend fun httpLogin(@Body map: Map<String,String>): ApiResponse<InformationEntity>

    /**
     * send sms
     */
    @POST("/v1/api-app/app-pwd-forgot-sendSms/{mobileNumber}")
    suspend fun httpSendCode(@Path("mobileNumber") mobile: String): ApiResponse<String?>

    /**
     * check code
     */
    @POST("/v1/api-app/app-pwd-forgot-checkSms/{mobileNumber}/{code}")
    suspend fun httpCheckCode(@Path("mobileNumber") mobile: String,
                              @Path("code") code: String): ApiResponse<Any>

    /**
     * resetPassWord
     * code
     * mobileNumber
     * password
     */
    @PUT("/v1/api-app/app-pwd-resetPassWord")
    suspend fun httpResetPassword(@Body map: Map<String,String>): ApiResponse<Any>

    /**
     * check has password
     * mobileNumber
     */
    @POST("/v1/api-app/user-password-check")
    suspend fun httpCheckHasPassword(@Body map: Map<String, String>): ApiResponse<Boolean>

    /**
     * setting password
     * mobileNumber
     * password
     */
    @POST("/v1/api-app/user-password-set")
    suspend fun httpUserPassword(@Body map: Map<String, String>): ApiResponse<Any>

    /**
     * log out
     */
    @POST("/v1/api-app/user-logout")
    suspend fun httpUserLogOut(): ApiResponse<Any>

    /**
     * account delete
     */
    @DELETE("/v1/api-app/user-remove")
    suspend fun httpAccountDelete(): ApiResponse<Any>

    /**
     * today summary
     */
    @GET("/v1/api-app/today/actual-revenue-labour/{venueId}")
    suspend fun httpTodaySummary(@Path("venueId") venueId: String): ApiResponse<TodaySummaryEntity>

    /**
     * Account
     */
    @GET("/v1/api-app/userInfo")
    suspend fun httpAccountInformation(): ApiResponse<InformationEntity>

    /**
     * 今天的收入劳动力，目标与实际
     */
    @GET("/v1/api-app/today/revenue-target-actual/{venueId}")
    suspend fun httpGetTodayAllData(@Path("venueId") venueId: String): ApiResponse<TodayDataEntity>

    /**
     * revenue & labour - daily
     */
    @GET("/v1/api-app/today/revenue-target-actual-minutes/{venueId}")
    suspend fun httpGetTodayAllDataMinutes(@Path("venueId") venueId: String): ApiResponse<TodayDataEntity>

    /**
     * guidance
     */
    @GET("/v1/api-app/week/profit-revenue-labour/{venueId}")
    suspend fun httpGetGuidance(@Path("venueId") venueId: String): ApiResponse<GuidanceUIEntity>

    /**
     * revenue & labour - daily
     */
    @GET("/v1/api-app/today/revenue-target-actual/{venueId}")
    suspend fun httpGetDaily(@Path("venueId") venueId: String): ApiResponse<DailyEntity>


    /**
     * revenue & labour - week list
     */
    @GET("/v1/api-app/week/weekList/{venueId}")
    suspend fun httpGetWeekList(@Path("venueId") venueId: String): ApiResponse<List<WeekItemEntity>>

    /**
     * revenue & labour - week
     */
    @GET("/v1/api-app/week/revenue-labour-trends/{venueId}")
    suspend fun httpGetWeek(
        @Path("venueId") venueId: String): ApiResponse<WeekDetailsEntity>

    @POST("/v1/api-app/version-getLatestVersion")
    suspend fun httpCheckUpdate(@Body map: Map<String, String>): ApiResponse<UpdateEntity>
}