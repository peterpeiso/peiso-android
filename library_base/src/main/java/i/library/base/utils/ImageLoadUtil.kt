package i.library.base.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import i.library.base.net.glide.GlideBlurTransformation

/**
 * Created by hc. on 2021/11/10
 * Describe: 加载图片
 */
fun ImageView.loadBlurImage(draw : Int){
    val transform = RequestOptions()
        .transform(GlideBlurTransformation(context))
    Glide.with(this).load(draw).apply(transform).into(this)
}

fun ImageView.loadDrawImage(draw: Int){
    setImageResource(draw)
}

