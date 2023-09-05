package i.library.base.net.glide

import com.bumptech.glide.load.model.GlideUrl

/**
 * Created by forever_zero on 2022/8/2.
 **/
class CustomGlideUrl(val url: String): GlideUrl(url) {

    override fun getCacheKey(): String {
        return when{
            url.contains(".jpg") -> {
                url.substring(0,url.indexOf(".jpg"))
            }
            else -> url
        }
    }

}