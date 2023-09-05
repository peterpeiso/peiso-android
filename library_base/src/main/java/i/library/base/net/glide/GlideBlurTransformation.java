package i.library.base.net.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.security.MessageDigest;

/**
 * 模糊
 */
public class GlideBlurTransformation extends CenterCrop {

    private Context context;

    public GlideBlurTransformation(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = super.transform(pool, toTransform, outWidth, outHeight);
        JavaBlurBitmapUtil javaBlurBitmapUtil = new JavaBlurBitmapUtil();
        return javaBlurBitmapUtil.blurBitmap2(context, bitmap, 25, (int) (outWidth), (int) (outHeight));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) { }
}