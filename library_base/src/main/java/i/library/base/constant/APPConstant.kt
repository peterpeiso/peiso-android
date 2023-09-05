package i.library.base.constant

/**
 * Created by hc. on 2021/11/8
 * Describe: constant
 */

val TOKEN_NAME = "token"

const val HTTP_SUCCEED_COED = 0

const val ACTIVITY_STATE_TOOlBAR = 0
const val ACTIVITY_STATE_NO_TITLE = 1
const val ACTIVITY_STATE_IMMERSIVE = 2

const val TYPE_ANIMATION_NOT = 0
const val TYPE_ANIMATION_ALL = 1
const val TYPE_ANIMATION_START = 2
const val TYPE_ANIMATION_END = 3

/** loading finish **/
const val INFORM_LOAD_END = "INFORM_LOAD_END"
/** loading start **/
const val INFORM_LOAD_START = "INFORM_LOAD_START"

/** adapter type **/
const val ADAPTER_TYPE_HEAD = 0
const val ADAPTER_TYPE_ITEM = 1
const val ADAPTER_TYPE_FOOTER = 2

/** 加载模式 **/
const val LOAD_TYPE_COMMON = "LOAD_TYPE_COMMON"
const val LOAD_TYPE_LIST = "LOAD_TYPE_LIST"

/** 加载 - 列表完成加载 **/
const val INFORM_LOAD_LIST = "INFORM_LOAD_LIST"
/** 加载 - 列表加载状态 **/
const val INFORM_LOAD_STATE = "INFORM_LOAD_STATE"
/** 加载 - 列表完成加载 - 加载更多 **/
const val INFORM_LOAD_MORE = "INFORM_LOAD_MORE"

const val STATE_FINISH = "STATE_FINISH"

/**
8902 Your account has been delete
8901 Your account has been disable
90001 Account is logged in on another device
90002 Token is Null
90000 Token Expired
8902 Your account has been delete
8901 Your account has been disable
90001 Account is logged in on another device
90002 Token is Null
90000 Token Expired
5001 This account does not exist
 */

/** 令牌无效 **/
const val CODE_TOKEN_INVALID = 5001
/** 令牌过期 **/
const val CODE_TOKEN_EXPIRED = 9000
/** 异地登录 **/
const val CODE_TOKEN_ELSE = 90001
/** 令牌错误 **/
const val CODE_TOKEN_NULL = 90002
/** 账号被禁用 **/
const val CODE_TOKEN_FORBIDDEN = 8901
/** 账号被注销 **/
const val CODE_TOKEN_DELETE = 8902

/** **/
const val LOGIN_CODE_DEFAULT = 0