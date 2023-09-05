package i.library.base.net.retrofit.sign;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by StomHong on 2018/7/31
 * <br/>
 * Preference帮助类
 */

public final class PreferenceValues {

    public PreferenceValues() {
    }

    private static final String DEFAULT_PREFERENCE_NAME = "VaraUser";

    /**
     * 获得程序默认{@link SharedPreferences}
     *
     * @param context
     * @return
     */
    public static SharedPreferences GetDefaultPreferences(Context context) {
        return context.getSharedPreferences(DEFAULT_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 用来保存全局变量如uid什么的。只能保存String, int, long, float,
     * boolean的值,StringSet的话自己拿
     *
     * @param context
     * @param key
     * @param value   如果是null的话都会被保存为String
     * @return Returns true if the new values were successfully written to
     * persistent storage.
     */
    public static boolean SaveValue(Context context, String key, Object value) {
        synchronized (DEFAULT_PREFERENCE_NAME) {
            SharedPreferences.Editor editor = GetDefaultPreferences(context).edit();
            if (value instanceof String)
                editor.putString(key, (String) value);
            else if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof Float)
                editor.putFloat(key, (Float) value);
            else if (value instanceof Long)
                editor.putLong(key, (Long) value);
            else if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);
            else if (value == null)
                editor.putString(key, null);
            return editor.commit();
        }
    }

    /**
     * 删除全局变量譬如uid什么的。
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean RemoveValue(Context context, String key) {
        synchronized (DEFAULT_PREFERENCE_NAME) {
            SharedPreferences.Editor editor = GetDefaultPreferences(context).edit();
            editor.remove(key);
            return editor.commit();
        }
    }

    /**
     * 清空保存的参数
     *
     * @param context
     */
    public static void ClearValues(Context context) {
        SharedPreferences.Editor editor = GetDefaultPreferences(context).edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存接口返回的Pass
     *
     * @param context
     * @param pass
     */
    public static void saveAppPass(Context context, String pass) {
        SaveValue(context, "app_pass", pass);
    }

    /**
     * 获取Pass，用于网络请求，如果没有则""
     *
     * @param context
     * @return
     */
    public static String getAppPass(Context context) {
        return GetDefaultPreferences(context).getString("app_pass", "");
    }

    /**
     * 清空pass
     *
     * @param context
     * @return
     */
    public static Boolean removeAppPass(Context context) {
        return RemoveValue(context,"app_pass");
    }

    /**
     * 保存接口返回的用户信息
     *
     * @param context
     * @param userBean
     */
    public static void saveUserInfo(Context context, String userBean) {
        SaveValue(context, "user_info", userBean);
    }

    /**
     * 获取用户信息，用于网络请求，如果没有则""
     *
     * @param context
     * @return
     */
    public static String getUserInfo(Context context) {
        return GetDefaultPreferences(context).getString("user_info", "");
    }

    /**
     * 清空用户信息
     *
     * @param context
     * @return
     */
    public static Boolean removeUserInfo(Context context) {
        return RemoveValue(context,"user_info");
    }

    /**
     * 保存uuid
     *
     * @param context
     * @param pass
     */
    public static void saveUUID(Context context, String pass) {
        SaveValue(context, "uuid", pass);
    }

    /**
     * 获取uuid
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        return GetDefaultPreferences(context).getString("uuid", "");
    }

    /**
     * 清空uuid
     *
     * @param context
     * @return
     */
    public static Boolean removeUUID(Context context) {
        return RemoveValue(context,"uuid");
    }

    /**
     * 保存更新时 以后再说的时间
     *
     * @param context
     * @param time
     */
    public static void saveUpdateLaterTime(Context context, long time) {
        SaveValue(context, "later_time", time);
    }

    /**
     * 获取更新时 以后再说的时间
     *
     * @param context
     * @return
     */
    public static long getUpdateLaterTime(Context context) {
        return GetDefaultPreferences(context).getLong("later_time", 0);
    }

    /**
     * 首页选中的纬度
     *
     * @param context
     * @param latitude
     */
    public static void saveLatitude(Context context, String latitude) {
        SaveValue(context, "latitude",latitude);
    }

    /**
     * 首页选中的纬度
     *
     * @param context
     * @return
     */
    public static String getLatitude(Context context) {
        return GetDefaultPreferences(context).getString("latitude","");
    }

    /**
     * 首页选中的经度
     *
     * @param context
     * @param longitude
     */
    public static void saveLongitude(Context context, String longitude) {
        SaveValue(context, "longitude", longitude);
    }

    /**
     * 首页选中的经度
     *
     * @param context
     * @return
     */
    public static String getLongitude(Context context) {
        return GetDefaultPreferences(context).getString("longitude","");
    }

    /**
     * 保存 当前定位的纬度
     *
     * @param context
     * @param latitude
     */
    public static void saveLocationLatitude(Context context, String latitude) {
        SaveValue(context, "location_latitude",latitude);
    }

    /**
     * 获取 当前定位的纬度
     *
     * @param context
     * @return
     */
    public static String getLocationLatitude(Context context) {
        return GetDefaultPreferences(context).getString("location_latitude","");
    }

    /**
     * 保存 当前定位的经度
     *
     * @param context
     * @param longitude
     */
    public static void saveLocationLongitude(Context context, String longitude) {
        SaveValue(context, "location_longitude", longitude);
    }

    /**
     * 获取当前定位的经度
     *
     * @param context
     * @return
     */
    public static String getLocationLongitude(Context context) {
        return GetDefaultPreferences(context).getString("location_longitude","");
    }

    /**
     * 保存 搜索商品记录
     *
     * @param context
     * @param searchProducts
     */
    public static void saveSearchProducts(Context context, String storeId, String searchProducts) {
        SaveValue(context, storeId, searchProducts);
    }

    /**
     * 获取 搜索商品记录
     *
     * @param context
     * @return
     */
    public static String getSearchProducts(Context context, String storeId) {
        return GetDefaultPreferences(context).getString(storeId, "");
    }

    /**
     * 清除 搜索商品记录
     *
     * @param context
     * @return
     */
    public static boolean removeSearchProducts(Context context, String storeId) {
        return RemoveValue(context, storeId);
    }

    /**
     * 保存 搜索店铺记录
     *
     * @param context
     * @param searchStore
     */
    public static void saveSearchStore(Context context, String searchStore) {
        SaveValue(context, "search_store", searchStore);
    }

    /**
     * 获取 搜索店铺记录
     *
     * @param context
     * @return
     */
    public static String getSearchStore(Context context) {
        return GetDefaultPreferences(context).getString("search_store","");
    }

    /**
     * 清除 搜索店铺记录
     *
     * @param context
     * @return
     */
    public static boolean removeSearchStore(Context context) {
        return RemoveValue(context, "search_store");
    }

    /**
     * 保存 搜索地址记录
     *
     * @param context
     * @param searchAddress
     */
    public static void saveSearchAddress(Context context, String searchAddress) {
        SaveValue(context, "search_address", searchAddress);
    }

    /**
     * 获取 搜索地址记录
     *
     * @param context
     * @return
     */
    public static String getSearchAddress(Context context) {
        return GetDefaultPreferences(context).getString("search_address","");
    }

    /**
     * 清除 搜索地址记录
     *
     * @param context
     * @return
     */
    public static boolean removeSearchAddress(Context context) {
        return RemoveValue(context, "search_address");
    }
}
