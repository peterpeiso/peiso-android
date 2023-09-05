package i.library.base.entity

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import i.library.base.adapter.ItemIDInterface
import i.library.base.base.BaseApplication
import i.library.base.utils.AMathUtils

/**
 * Created by hc. on 2021/12/6
 * Describe:
 */
data class IVEntity(val key: Int,val value: String)

data class KVEntity(val key: String,val value: String): ItemIDInterface{
    override fun getItemID(): Long {
        return AMathUtils.createFakeID(key)
    }
}

data class KBEntity(val key: String,val value: Boolean)

data class NVDEntity(val name: String,val value: String,val draw: Int ?= -1): ItemIDInterface{

    var moreData: Any? = null

    fun iGetDrawable(): Drawable?{
        return draw.iGetDrawable()
    }

    override fun getItemID(): Long {
        return AMathUtils.createFakeID(name)
    }

}

data class TabDataEntity(val name: String,val select: Int)

data class NavGraphEntity(val id: Int,val bundle: Bundle)

data class IVDEntity(val key: Int,val value: String,val draw: Int ?= -1){
    fun iGetDrawable(): Drawable?{
        return draw.iGetDrawable()
    }
}

fun Int?.iGetDrawable(): Drawable?{
    if(this == null || this == -1) return null
    return ContextCompat.getDrawable(BaseApplication.getInstance(),this)
}