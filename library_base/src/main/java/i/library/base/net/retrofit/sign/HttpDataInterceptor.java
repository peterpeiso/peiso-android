package i.library.base.net.retrofit.sign;


import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import i.library.base.base.BaseApplication;
import i.library.base.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class HttpDataInterceptor implements Interceptor {

    public static String NOT_SIGN = "RETROFIT_TAG_SIGN_NOT";
    public static String NOT_BASE = "RETROFIT_TAG_BASE_NOT";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request requestBack;
        Object tag = request.tag(String.class);
        if(tag == null){
            requestBack = getSignData(request);
        }else{
            String s = tag.toString();
            if(s.equals(NOT_SIGN)) {
                requestBack = getOrdinaryData(request);
            }else{
                requestBack = getSignData(request);
            }
        }
        return chain.proceed(requestBack);
    }

    /** 不签名 **/
    private Request getOrdinaryData(Request request) throws IOException {
        BaseApplication instance = BaseApplication.Companion.getInstance();
        String versionCode = "";
        Map<String, String> map = new HashMap<String, String>();
        map.put("appType", "1");
        map.put("platformType", "1");
        map.put("versionCode", versionCode);
        Request.Builder builder = request.newBuilder();
        String token = BaseApplication.Companion.iGetLoginManager().iGetToken();
        if (!TextUtils.isEmpty(token))
            builder.addHeader("token", token);
        String versionInfo = Base64.encode(map.toString().getBytes("UTF-8"));
        builder.addHeader("user-agent", "android");
        request = builder.addHeader("VersionInfo", versionInfo).build();
        return request;
    }

    /** 签名 **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Request getSignData(Request request) throws IOException {
        BaseApplication instance = BaseApplication.Companion.getInstance();
        String appType = "0";
        String platformType = "1";
        String uuid = getDeviceId();
        String versionCode = BuildConfig.VERSION_NAME;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appType", appType);
        jsonObject.put("platformType", platformType);
        jsonObject.put("versionCode", versionCode);
        jsonObject.put("uuid", uuid);
        jsonObject.put("deviceId", uuid);

        Request.Builder builder = request.newBuilder();
        builder.addHeader("VersionInfo", Base64.encode(jsonObject.toString().getBytes()));
        Log.e("Tag", "intercept: "+jsonObject.toString());
        String nonceStr = new RandomString(16).nextString();//随机字符串
        String utcStr = String.valueOf(System.currentTimeMillis());//时间戳
        builder.addHeader("nonceStr", nonceStr);
        builder.addHeader("utcStr", utcStr);
        builder.addHeader("user-agent", "android");
        RequestBody body = request.body();
        boolean isFile = body != null
                && body.contentType() != null
                && body.contentType().toString().contains("multipart/form-data");
        if (isFile) {
            request = builder.build();
        } else {
            Set<String> queryKeyNames = request.url().queryParameterNames();
            //进行排序
            TreeMap<String, String> paramMap = new TreeMap<>();
            RequestBody requestBody = body;
            if (requestBody != null) {
                Buffer bufferedSink = new Buffer();
                request.body().writeTo(bufferedSink);
                String content = bufferedSink.readString(Charset.forName("UTF-8"));
                Log.e("Tag", "intercept: "+content );
                String[] split = content.split("&");
                try{
                    if (split.length > 0 ){
                        for (String s : split){
                            if (!("".equals(s))){
                                String key = s.substring(0, s.indexOf("="));
                                String value = s.substring(s.indexOf("=")+1);
                                paramMap.put(key, value);
                            }
                        }
                    }
                }catch (Exception e){
                    try {
                        JSONObject requestJsonObject = JSONObject.parseObject(content);
                        if (requestJsonObject != null) {
                            for (Map.Entry<String, Object> entry : requestJsonObject.entrySet()) {
                                if (entry.getValue() != null) {
                                    paramMap.put(entry.getKey(), entry.getValue().toString());
                                }
                            }
                        }
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
            paramMap.put("nonceStr", nonceStr);//随机字符串
            paramMap.put("utcStr", utcStr);//时间戳
            for (String s : queryKeyNames) {
                Log.e("tag", "intercept: "+s );
                paramMap.put(s, request.url().queryParameter(s));
            }
            Iterator iterator = paramMap.entrySet().iterator();
            StringBuffer buffer = new StringBuffer();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (!TextUtils.isEmpty(value)) {
                    buffer.append(key + "=" + value);
                    buffer.append("&");
                }
            }
            buffer.append("key=fairfiled@technehq.com@2021");//这是和后端约定好的key
            builder.addHeader("Sign", AppUtils.md5(buffer.toString()).toUpperCase());
            builder.addHeader("Content-Type", "application/json;charset=utf-8");
            request = builder.build();
        }
        return request;
    }

    /** 设备号 **/
    public static String getDeviceId() {
        return Settings.System.getString(
                BaseApplication.Companion.getInstance().getContentResolver(),
                Settings.System.ANDROID_ID);
    }
}
