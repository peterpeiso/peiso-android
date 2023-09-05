package i.library.base.more

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import i.library.base.R
import i.library.base.base.BaseApplication
import i.library.base.constant.TYPE_ANIMATION_ALL
import i.library.base.constant.TYPE_ANIMATION_END
import i.library.base.constant.TYPE_ANIMATION_START
import i.library.base.net.glide.CustomGlideUrl
import i.library.base.utils.ScreenUtils
import i.library.base.utils.createGradientDrawable
import i.library.base.utils.createGradientLineDrawable


/**
 * Created by hc. on 2021/11/8
 * Describe: BindingAdapter
 */

/** circle image **/
@BindingAdapter("loadCircleImageUrl","defaultCircleImageDraw",requireAll = false)
fun bindCircleImage(imageView: ImageView,url: String?, draw: Drawable?){
    if(url?.startsWith("http") == false){
        bindingBGFile(imageView,url,999f)
        return
    }
    val realUrl = url?:"-"
    Glide.with(imageView)
        .load(CustomGlideUrl(realUrl))
        .transition(withCrossFade())
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
}


@BindingAdapter("bindImageDraw")
fun bindImageDraw(imageView: ImageView,draw: Int?){
    if(draw == -1 || draw == null){
        imageView.setImageDrawable(null)
    }else{
        imageView.setImageResource(draw)
    }
}

@BindingAdapter("loadImageUrl","defaultImageDraw","radius",requireAll = false)
fun bindImageUrl(imageView: ImageView,url: String?, draw: Drawable?,radius: Int?){
    val realDefaultDraw = draw
        ?:ContextCompat.getDrawable(BaseApplication.getInstance(),R.drawable.drawable_image_null)
    val realUrl = url?:""
    val realRadius = radius?:0
    if(url == null || TextUtils.isEmpty(url)){
        Glide.with(imageView.context)
            .load(realDefaultDraw)
            .into(imageView)
        return
    }
    val transform = if(realRadius == 0){
        CenterCrop()
    }else{
        MultiTransformation(
            CenterCrop(),
            RoundedCorners(ScreenUtils.dip2px(realRadius * 1f)))
    }
    val options =
        RequestOptions()
            .transform(transform)
            .placeholder(realDefaultDraw)
    Glide.with(imageView.context)
        .load(realUrl)
        .apply(options)
        .into(imageView)
}

@BindingAdapter("bindingBGPath","bindBGRadius",requireAll = false)
fun bindingBGFile(imageView: ImageView,path: String,radius: Float?){
    val realRadius = ScreenUtils.dip2px(radius?:15f)
    val transform = MultiTransformation(
        CenterCrop(),
        RoundedCorners(ScreenUtils.dip2px(realRadius * 1f)))
    val options =
        RequestOptions()
            .transform(transform)
    Glide.with(imageView.context)
        .load(path)
        .apply(options)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(imageView)
}

/** background **/
@BindingAdapter("bindBGColor","bindBGRadius","bindBGIsLine",requireAll = false)
fun bindBGDrawable(
    iSetView: View,
    @ColorInt color: Int,
    radius : Float?,
    isLine : Boolean?){
    val realRadius = ScreenUtils.dip2px(radius?:15f) * 1f
    val realIsLine = isLine?:false
    val drawable = if(!realIsLine){
        createGradientDrawable(color, realRadius)
    }else{
        createGradientLineDrawable(color,realRadius)
    }
    iSetView.background = drawable
}


/**
 * 显示和隐藏
 * animation 0我都不要   1 我全都要 2 要开始 3 要结束
 */
@BindingAdapter("bindVisibility","bindAnimationType" ,requireAll = false)
fun bindVisibility(view : View, visibility: Boolean, animationType : Int?) {
    if (visibility) {
        view.visibility = View.VISIBLE
        if (animationType == TYPE_ANIMATION_ALL || animationType == TYPE_ANIMATION_START) {
            val animation = AlphaAnimation(0f, 1f)
            animation.duration = 300
            view.animation = animation
        }
    } else {
        if (animationType == TYPE_ANIMATION_ALL || animationType == TYPE_ANIMATION_END) {
            val animation = AlphaAnimation(1f, 0f)
            animation.duration = 300
            view.animation = animation
        }
        view.visibility = View.GONE
    }
}

@BindingAdapter("checkVisibilityStr")
fun bindVisibility(view : View, checkVisibilityStr: String?){
    view.isVisible = !TextUtils.isEmpty(checkVisibilityStr)
}
@BindingAdapter("checkVisibilityStrI")
fun bindVisibilityI(view : View, checkVisibilityStr: String?){
    view.visibility = if(TextUtils.isEmpty(checkVisibilityStr)) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("bindingHeight")
fun bindingHeight(view : View, height: Int?){
    height?:return
    val viewHeight = view.height
    if(viewHeight == height) return
    ValueAnimator.ofInt(viewHeight,height).apply {
        addUpdateListener {
            val to = it.animatedValue as Int
            view.layoutParams.height = to
            view.requestLayout()
        }
    }.start()
}