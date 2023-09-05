package i.library.base.net.retrofit.sign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AppUtils {

    public static String toGetMD5Password(String mobile, String password){
        String temp;
        if(mobile.startsWith("0")){
            temp = mobile.substring(1);
        }else{
            temp = mobile;
        }
        return md5(temp + password);
    }

    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获取uuid
     * @param context
     * @return
     */
    public static String getUUID(Context context){
        if (TextUtils.isEmpty(PreferenceValues.getUUID(context))){
            PreferenceValues.saveUUID(context, UUID.randomUUID().toString());
        }
        return PreferenceValues.getUUID(context);
    }

    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }
}
