package i.library.base.base.recycle

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import i.library.base.base.BaseViewModel
import i.library.base.constant.*
import i.library.base.entity.ApiResponse
import i.library.base.expands.toast
import i.library.base.net.retrofit.APIRequest
import i.library.base.net.retrofit.BaseRetrofitService
import i.library.base.net.retrofit.RetrofitUtils


/**
 * Created by hc. on 2020/5/22
 * Describe: 通用的列表加载
 */
abstract class BaseRecycleVModel : BaseViewModel() {

    companion object{
        const val DEFAULT_MIN_PAGE = 1
        private const val DEFAULT_PAGE_SIZE = 10

        /** 加载完成 **/
        const val LOAD_NOT = -1
        /** 加载开始 **/
        const val LOAD_START = 0
        /** 加载结束 **/
        const val LOAD_END = 1
        /** 加载异常 **/
        const val LOAD_ERROR = -1
        /** 加载完成 **/
        const val LOAD_OVER = 2
    }
    /** 请求类型 **/
    var mRequestType = "GET"
    /** 最小页码 **/
    var mMinPage = DEFAULT_MIN_PAGE
    /** 当前页码 **/
    private var mPage = mMinPage
    /** 每页数量 **/
    private var mPageSize = DEFAULT_PAGE_SIZE
    /** 是否正在加载中 **/
    var isLoadUnderway = false
    /** 加载完成 **/
    var isLoadOver = false
    /** 是否使用默认的分页加载参数 **/
    protected var isUsePage = true
    /** 是否不进行网络请求 **/
    var isTest = false

    /** URL 地址 **/
    abstract fun getHttpUrl() : String

    /** 请求结束处理请求 **/
    abstract fun refreshData(json: String?) : List<*>

    /** 请求参数 **/
    open fun getParameter() : HashMap<String,Any>{
        return HashMap()
    }

    fun getRecyclePage() : Int{ return mPage }

    open fun getPageSize() : Int{
        return DEFAULT_PAGE_SIZE
    }

    abstract fun iGetBaseUrl(): String

    /** 处理异常 **/
    open fun loadError(entity: ApiResponse<JsonElement>?) {
        /** 状态改变 **/
        refreshData(null)
        /** 改变状态 - 加载异常 **/
        toInform(INFORM_LOAD_STATE, LOAD_ERROR)
        /** 输出异常 **/
        entity?.message?:"Connection failure.".toast()
    }

    private suspend fun initRetrofitRequest() : ApiResponse<JsonElement> {
        mPageSize = getPageSize()
        val parameter = getParameter()
        if(isUsePage){
            parameter["pageNum"] = mPage
            parameter["pageSize"] = mPageSize
        }
        return when(mRequestType){
            "POST" -> {
                RetrofitUtils.getRetrofit(iGetBaseUrl()).create(BaseRetrofitService::class.java)
                    .httpPostList(getHttpUrl(),parameter)
            }
            "POST_FIELD" -> {
                RetrofitUtils.getRetrofit(iGetBaseUrl()).create(BaseRetrofitService::class.java)
                    .httpPostListField(getHttpUrl(),parameter)
            }
            else -> {
                RetrofitUtils.getRetrofit(iGetBaseUrl()).create(BaseRetrofitService::class.java)
                    .httpGetList(getHttpUrl(),parameter)
            }
        }
    }

    /** 加载数据 **/
    open fun loadData(){
        /** 正在加载中 **/
        if(isLoadUnderway){
            return
        }
        /* 加载更多 */
        val isLoadMore = mPage != mMinPage
        /** 是否测试 **/
        if(isTest){
            loadNext(null,isLoadMore)
            toInform(INFORM_LOAD_STATE, LOAD_END)
            toInform(INFORM_LOAD_END,",$LOAD_TYPE_LIST")
            isLoadUnderway = false
            return
        }
        /** 改变状态 - 加载开始 **/
        isLoadUnderway = true
        toInform(INFORM_LOAD_STATE, LOAD_START)
        APIRequest<JsonElement>(this)
            .request {
                initRetrofitRequest()
            }
            .registerSucceed {
                loadNext(it,isLoadMore)
            }
            .registerError {
                loadError(it)
                initLoadPage()
            }
            .registerFailure {
                loadError(null)
                initLoadPage()
            }
            .registerFinish {
                isLoadUnderway = false
            }
            .start()
    }

    open fun loadNext(entity: JsonElement?,isLoadMore : Boolean){
        val array = refreshData(entity?.toString()?:"")
        /** 是否加载完全部 **/
        isLoadOver = array.size != mPageSize
        /** 改变状态 - 加载完成 **/
        toInform(INFORM_LOAD_STATE,if(isLoadOver) LOAD_OVER else LOAD_END)
        if(array.isEmpty()){
            initLoadPage()
        }
        /** 通知数据刷新 **/
        if(isLoadMore){
            toInform(INFORM_LOAD_MORE,array)
        }else{
            toInform(INFORM_LOAD_LIST,array)
        }
    }

    /** 没有获得新的数据 **/
    fun initLoadPage(){
        if(mPage > 1){
            mPage--
        }
    }

    /** 初始化加载 **/
    fun initRefresh(){
        mPage = mMinPage
        loadData()
    }

    /** 刷新本列表数据 **/
    fun refreshList(){
        mPageSize = getPageSize() * mPage
        mPage = mMinPage
        loadData()
        mPageSize = getPageSize()
    }

    /** 加载更多 **/
    fun loadMore(){
        if(isLoadUnderway){
            return
        }
        mPage ++
        loadData()
    }

    /** 直接初始化数据 **/
    fun initRecycleData(list : List<*>){
        toInform(INFORM_LOAD_LIST,list)
    }

    fun <T> jsonToList(
        json: String?,
        cls: Class<T>?,
        name: String = "list"
    ): ArrayList<T>? {
        if(TextUtils.isEmpty(json)) return null
        val parse = JsonParser().parse(json)
        if(parse.isJsonNull) return null
        val jsonObject = parse.asJsonObject
        val jsonArray = jsonObject.getAsJsonArray(name)
        val list: ArrayList<T> = ArrayList()
        for (jsonElement in jsonArray) {
            list.add(Gson().fromJson(jsonElement, cls))
        }
        return list
    }

    fun <T> iJsonToList(
        json: String?,
        cls: Class<T>?
    ): List<T> {
        if(TextUtils.isEmpty(json)) return ArrayList()
        val parse = JsonParser().parse(json)
        if(parse.isJsonNull) return ArrayList()
        val jsonArray = parse.asJsonArray
        val list: MutableList<T> = ArrayList()
        for (jsonElement in jsonArray) {
            list.add(Gson().fromJson(jsonElement, cls))
        }
        return list
    }
}

/** 列表返回 **/
//data class ListGetEntity(val path : String, val parame : HashMap<String,Any>, val list : List<*>)